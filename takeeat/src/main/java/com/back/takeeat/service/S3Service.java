package com.back.takeeat.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //S3Config에서 @Bean으로 등록한 s3Client 주입
    private final AmazonS3 s3Client;

    //파일 확장자 리스트
    private final List<String> allowedFileExtensionList = Arrays.asList("jpg", "jpeg", "png", "gif");

    /**
     * AWS S3 파일 업로드 로직
     * @param multipartFiles -> 파일 객체 리스트
     * @return S3에 저장된 이미지의 경로(URL) 리스트
     */
    public List<String> uploadFile(List<MultipartFile> multipartFiles) {
        //S3 저장된 이미지의 URL을 담을 List 선언
        List<String> imgUrlList = new ArrayList<>();

        //파일의 수만큼 반복
        multipartFiles.forEach(file -> imgUrlList.add(this.uploadSingleFile(file)));

        return imgUrlList;
    }

    public String uploadSingleFile(MultipartFile file) {
        //중복을 피하기 위한 새로운 파일이름 생성
        String fileName = createFileName(file.getOriginalFilename());
        //ObjectMetadata -> S3에 업로드되는 파일 또는 객체의 관련된 정보를 저장하는 객체
        ObjectMetadata objectMetadata = new ObjectMetadata();
        //S3에 업로드할 파일의 사이즈 저장
        objectMetadata.setContentLength(file.getSize());
        //S3에 업로드할 파일의 contentType 저장
        objectMetadata.setContentType(file.getContentType());

        //파일 데이터를 stream으로 읽어 inputStream에 저장
        try(InputStream inputStream = file.getInputStream()) {
            //S3를 구분할 버킷명과 파일이름, 파일의 stream데이터, ObjectMetadata를 통해 S3 버킷에 업로드
            s3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch (IOException e) {
            throw new RuntimeException();
        }

        return s3Client.getUrl(bucket, fileName).toString();
    }


    public void deleteFiles(List<String> fileNames) {
        fileNames.forEach(fileName -> {
            try {
                s3Client.deleteObject(bucket, fileName.split("/")[3]);
            }catch (SdkClientException e) {
                throw new RuntimeException();
            }
        });//foreach end

    }

    /**
     * 파일명을 UUID 랜덤 값으로 변경
     * @param fileName 원본 파일명
     * @return UUID 랜덤 값이 적용된 파일명
     */
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    /**
     * 파일명에서 확장자를 추출
     * @param fileName 원본 파일명
     * @return 파일 확장자 반환
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if(lastDotIndex == -1) {
            throw new RuntimeException();
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();

        if(!allowedFileExtensionList.contains(extension)) {
            throw new RuntimeException();
        }

        return fileName.substring(lastDotIndex);

    }

    public String uploadBase64Image(String base64Image) throws IOException {
        // Base64 문자열에서 바이트 배열로 디코딩
        byte[] imageBytes = decodeBase64(base64Image);

        // S3에 업로드할 키 생성 (UUID를 사용하여 고유한 이름을 생성)
        String key = "images/" + UUID.randomUUID() + ".jpg";

        // PutObjectRequest 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key,
                new ByteArrayInputStream(imageBytes), null);

        // 객체를 S3에 업로드
        s3Client.putObject(putObjectRequest);

        // 업로드한 객체의 URL 반환
        return s3Client.getUrl(bucket, key).toString();
    }

    private byte[] decodeBase64(String base64Image) {
            return Base64.getDecoder().decode(base64Image);
    }

}

