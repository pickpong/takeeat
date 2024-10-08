package com.back.takeeat.service;

import com.back.takeeat.common.exception.EntityNotFoundException;
import com.back.takeeat.common.exception.ErrorCode;
import com.back.takeeat.domain.market.Market;
import com.back.takeeat.domain.market.MarketStatus;
import com.back.takeeat.domain.menu.Menu;
import com.back.takeeat.domain.menu.MenuCategory;
import com.back.takeeat.domain.option.Option;
import com.back.takeeat.domain.option.OptionCategory;
import com.back.takeeat.domain.review.Review;
import com.back.takeeat.domain.user.Member;
import com.back.takeeat.dto.market.request.*;
import com.back.takeeat.dto.market.response.MarketHomeResponse;
import com.back.takeeat.dto.market.response.MarketReviewResponse;
import com.back.takeeat.dto.market.response.MenuCategoryNameResponse;
import com.back.takeeat.dto.review.response.MarketRatingResponse;
import com.back.takeeat.dto.review.response.RatingCountResponse;
import com.back.takeeat.dto.review.response.ReviewResponse;
import com.back.takeeat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MarketService {

    private final MarketRepository marketRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final OptionCategoryRepository optionCategoryRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final S3Service s3Service;

    public void marketInfoRegister(MarketInfoRequest marketInfoRequest, Long memberId, List<String> imgUrls) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_SAVE));
        Market market = marketInfoRequest.toMarket(member);

        for(String imgUrl : imgUrls) {
            market.addMarketImage(imgUrl);
        }
        // 마켓 처음 등록시 close
        market.addMarketStatus(MarketStatus.CLOSE);

        marketRepository.save(market);
    }

    @Transactional(readOnly = true)
    public boolean checkMarketNameDuplicate(String marketName) {
        return marketRepository.existsByMarketName(marketName);
    }

    public void menuCategoriesRegister(MenuRequest menuRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NoSuchElementException::new);
        Market market = marketRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));

        for (MarketMenuCategoryRequest marketMenuCategoryRequest : menuRequest.getCategories()) {
            MenuCategory menuCategory = marketMenuCategoryRequest.toMenuCategory(market);
            // 디버깅 포인트: 메뉴 카테고리 정보 출력
            System.out.println("메뉴 카테고리 저장: " + menuCategory.getMenuCategoryName());

            for (MarketMenuRequest marketMenuRequest : marketMenuCategoryRequest.getMenus()) {

                // base64 이미지를 S3에 업로드 한 뒤 생성된 URL로 이미지 이름 변경
                try {
                    String imgUrls = s3Service.uploadBase64Image(marketMenuRequest.getMenuImage());
                    marketMenuRequest.menuImageToUrl(imgUrls);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Menu menu = marketMenuRequest.toMenu();

                // 디버깅 포인트: 메뉴 정보 출력
                System.out.println("메뉴 추가: " + menu.getMenuName());

                menu.addMenuCategory(menuCategory);
                menuCategory.getMenus().add(menu);
            }

            // 데이터 베이스 저장
            menuCategoryRepository.save(menuCategory);

            // 디버깅 포인트: 저장된 메뉴 카테고리 확인
            System.out.println("메뉴 카테고리 저장 완료: " + menuCategory.getId());
        }
    }

    public List<MenuCategoryNameResponse> getMarketMenuName(Long memberId){
        // 회원 정보를 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 마켓 정보를 조회
        Market market = marketRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));

        if (market == null) {
            throw new NoSuchElementException("마켓 정보를 찾을 수 없습니다.");
        }

        // 메뉴 카테고리 정보를 조회
        List<MenuCategoryNameResponse> menuCategoryList = menuCategoryRepository.findMenuCategoriesByMarketId(market.getId());

        // 결과 리스트를 저장할 리스트 초기화
        List<MenuCategoryNameResponse> menuCategoryNameResponses = new ArrayList<>();

        // 각 메뉴 카테고리에 대해 메뉴 정보를 조회하고 결과 리스트에 추가
        for(MenuCategoryNameResponse menuCategoryNameResponse : menuCategoryList) {
            if (!menuCategoryList.contains(menuCategoryNameResponse.getMenuCategoryId())) {
                menuCategoryNameResponses.add(menuCategoryNameResponse.create());
            } else {
                menuCategoryNameResponses.add(menuCategoryList.get(menuCategoryNameResponse.getMenuCategoryId().intValue()));
            }

        }

        return menuCategoryNameResponses;
    }

    public void optionCategoriesRegister(OptionRequest optionRequest, Long memberId) {
        // 회원 정보를 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 마켓 정보를 조회
        Market market = marketRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));

        // 메뉴 카테고리를 조회
        List<MenuCategory> menuCategoryList = menuCategoryRepository.findByMarketId(market.getId());

        for (MarketOptionCategoryRequest marketOptionCategoryRequest : optionRequest.getCategories()) {
            // 적절한 메뉴를 찾기 위한 로직 필요
            Menu menu = menuCategoryList.stream()
                    .flatMap(mc -> menuRepository.findByMenuCategoryId(mc.getId()).stream())
                    .filter(m -> m.getId().equals(marketOptionCategoryRequest.getMenuId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

            OptionCategory optionCategory = marketOptionCategoryRequest.toOptionCategory(menu);

            System.out.println("옵션 카테고리 저장: " + optionCategory.getOptionCategoryName());

            for (MarketOptionRequest marketOptionRequest : marketOptionCategoryRequest.getOptions()) {
                Option option = marketOptionRequest.toOption();
                System.out.println("옵션 추가: " + option.getOptionName());
                option.addOptionCategory(optionCategory);
                optionCategory.getOptions().add(option);
            }

            optionCategoryRepository.save(optionCategory);
            System.out.println("메뉴 카테고리 저장 완료: " + optionCategory.getId());
        }
    }
    
    @Transactional(readOnly = true)
    public MarketReviewResponse getReviewInfo(Long memberId) {

        Optional<Market> findMarket = marketRepository.findByMemberId(memberId);
        if (findMarket.isEmpty()) {
            return null;
        }

        Market market = findMarket.get();

        //Review
        List<Review> reviews = reviewRepository.findByMarketIdForReviewList(market.getId());

        //Review -> ReviewResponse(allOptionReviews)
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(ReviewResponse::createByReview)
                .collect(Collectors.toList());

        //reviewResponses -> noAnswerOptionReviews
        List<ReviewResponse> noAnswerOptionReviews = new ArrayList<>();
        for (ReviewResponse reviewResponse : reviewResponses) {
            if (reviewResponse.getOwnerReviewContent() == null) {
                noAnswerOptionReviews.add(reviewResponse);
            }
        }

        //blindOptionReviews
        List<Review> blindReviews = reviewRepository.findByMarketIdWithBlindStatus(market.getId());
        List<ReviewResponse> blindOptionReviews = blindReviews.stream()
                .map(ReviewResponse::createByReview)
                .collect(Collectors.toList());

        //Rating
        List<MarketRatingResponse> marketRatingResponses = reviewRepository.findRatingCountByMarketId(market.getId());
        RatingCountResponse ratingCountResponse = RatingCountResponse.createByMarketRatingResponse(marketRatingResponses);

        return MarketReviewResponse.create(market.getMarketRating(), ratingCountResponse, reviewResponses, noAnswerOptionReviews, blindOptionReviews);
    }

    @Transactional(readOnly = true)
    public MarketHomeResponse getMarketHome(Long memberId) {

        Optional<Market> findMarket = marketRepository.findByMemberId(memberId);
        if (findMarket.isEmpty()) {
            return null;
        }

        Market market = findMarket.get();

        return MarketHomeResponse.createByMarket(market);
    }

    public void updateStatus(Long memberId, MarketStatus marketStatus) {

        Market market = marketRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));

        market.updateMarketStatus(marketStatus);
    }

    public void menuImageRegister(List<String> imgUrls, Long memberId) {
        // 마켓 정보를 조회
        Market market = marketRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));
        List<MenuCategory> menuCategories = market.getMenuCategories();
        System.out.println("카테고리"+menuCategories);
        int idx = 0;
        for(MenuCategory menuCategory : menuCategories) {
            List<Menu> menus = menuCategory.getMenus();
            System.out.println("메뉴"+menus);
            for(Menu menu : menus) {
                menu.addMenuImage(imgUrls.get(idx));
                idx++;
            }
        }
    }


    @Transactional(readOnly = true)
    public Long findMarketId(Long memberId) {
        Market findMarket = marketRepository.findByMemberId(memberId)
                                        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MARKET_NOT_FOUND));

        return findMarket.getId();
    }

    // 마켓 존재 여부와 마켓 이름 중복 여부
    public boolean checkMarketIsExist(Long id, String marketName) {
        if(marketRepository.existsByMarketName(marketName) || marketRepository.existsByMemberId(id) ){
            return true;
        } else {
            return false;
        }
    }
      
}
