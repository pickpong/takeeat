package com.back.takeeat.service;

import com.back.takeeat.dto.mainPage.response.MarketInfoResponse;
import com.back.takeeat.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketListService {

    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public List<MarketInfoResponse> getMarketInfo(String marketCategory
                                                , double latitude, double longitude
                                                , String search, String sort) {
        // 반경 4.0km
        double distance = 4.0;

        // 1km당 위도와 경도의 변화량
        double deltaLat = distance / 111.0;
        double deltaLon = distance / (111.0 * Math.cos(Math.toRadians(latitude)));

        // 위도와 경도의 범위
        double minLat = latitude - deltaLat;
        double maxLat = latitude + deltaLat;
        double minLon = longitude - deltaLon;
        double maxLon = longitude + deltaLon;

        // 다른 정렬 선택 되었을 때
        if(sort.equals("review")) {
            if(marketCategory.equals("0")) {
                List<MarketInfoResponse> sortReviewAllMarkets = marketRepository.findAllByMarketReviewDesc(minLat, maxLat,
                        minLon, maxLon, search);
                return sortReviewAllMarkets;
            } else {
                List<MarketInfoResponse> sortReviewMarkets = marketRepository.findByMarketReviewDesc(marketCategory, minLat, maxLat,
                        minLon, maxLon, search);
                return sortReviewMarkets;
            }

        } else if(sort.equals("score")) {
            if(marketCategory.equals("0")) {
                List<MarketInfoResponse> sortScoreAllMarkets = marketRepository.findAllByMarketScoreDesc(minLat, maxLat,
                        minLon, maxLon, search);
                return sortScoreAllMarkets;
            } else {
                List<MarketInfoResponse> sortScoreMarkets = marketRepository.findByMarketScoreDesc(marketCategory, minLat, maxLat,
                        minLon, maxLon, search);
                return sortScoreMarkets;
            }
        // 기본 정렬인 거리순 일때
        } else {
            // 검색 값이 있고 전체보기일 때
            if (search != null && !search.isEmpty() && marketCategory.equals("0")) {
                List<MarketInfoResponse> searchFindAllMarkets = marketRepository.findAllMarketByContaining(minLat, maxLat,
                        minLon, maxLon, latitude, longitude, search);
                return searchFindAllMarkets;
                // 검색 값이 없고 전체보기일 때
            } else if (search.isEmpty() && search.equals("") && marketCategory.equals("0")) {
                List<MarketInfoResponse> findAllMarkets = marketRepository.findAllMarketByLatLon(minLat, maxLat, minLon,
                        maxLon, latitude, longitude);
                return findAllMarkets;
                // 검색 값이 있고 특정 카테고리보기일 때
            } else if (search != null && !search.isEmpty() && !marketCategory.equals("0")) {
                List<MarketInfoResponse> searchFindMarkets = marketRepository.findMarketByContaining(marketCategory, minLat,
                        maxLat, minLon, maxLon, latitude, longitude, search);
                return searchFindMarkets;
                // 검색 값이 없고 특정 카테고리보기일 때
            } else {
                List<MarketInfoResponse> findMarkets = marketRepository.findMarketByLatLon(marketCategory, minLat, maxLat,
                        minLon, maxLon, latitude, longitude);
                return findMarkets;
            }
        }
    }
}
