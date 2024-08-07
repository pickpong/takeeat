package com.back.takeeat.dto.cart.response;

import com.back.takeeat.domain.market.MarketStatus;
import com.back.takeeat.dto.cart.CartMenuIdAndOptionCategoryId;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class CartListResponse {

    private Long marketId;
    private String marketName;
    private MarketStatus marketStatus;

    private List<CartMenuResponse> cartMenuResponses;

    private Map<Long, List<CartOptionCategoryResponse>> optionCategoryByCartMenu;
    private Map<CartMenuIdAndOptionCategoryId, List<CartOptionResponse>> cartOptionMapByOptionCategoryId;

    public static CartListResponse create(Long marketId, String marketName, MarketStatus marketStatus, List<CartMenuResponse> cartMenuResponses,
                                          Map<Long, List<CartOptionCategoryResponse>> optionCategoryByCartMenu,
                                          Map<CartMenuIdAndOptionCategoryId, List<CartOptionResponse>> cartOptionMapByOptionCategoryId) {
        return CartListResponse.builder()
                .marketId(marketId)
                .marketName(marketName)
                .marketStatus(marketStatus)
                .cartMenuResponses(cartMenuResponses)
                .optionCategoryByCartMenu(optionCategoryByCartMenu)
                .cartOptionMapByOptionCategoryId(cartOptionMapByOptionCategoryId)
                .build();
    }

    public List<CartOptionResponse> getCartOptionInfo(Long cartMenuId, Long optionCategoryId) {
        CartMenuIdAndOptionCategoryId cao = new CartMenuIdAndOptionCategoryId(cartMenuId, optionCategoryId);
        return cartOptionMapByOptionCategoryId.get(cao);
    }
}
