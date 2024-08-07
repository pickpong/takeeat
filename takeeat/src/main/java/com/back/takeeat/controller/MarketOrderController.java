package com.back.takeeat.controller;

import com.back.takeeat.domain.order.OrderStatus;
import com.back.takeeat.domain.user.Member;
import com.back.takeeat.dto.marketorder.request.MarketOrderSearchRequest;
import com.back.takeeat.dto.marketorder.response.DetailMarketOrderResponse;
import com.back.takeeat.dto.marketorder.response.MarketOrdersResponse;
import com.back.takeeat.dto.marketorder.response.optioncategory.DetailMarketOrderResponseV2;
import com.back.takeeat.security.LoginMember;
import com.back.takeeat.service.MarketOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketOrderController {

    private final MarketOrderService marketOrderService;

    @GetMapping("/{marketId}/order")
    public String marketOrder(@PathVariable("marketId") Long marketId, @LoginMember Member member, Model model) {
        // TODO : 해당 경로로 접근하는 member가 해당 market의 주인인지 판별할 것(예외처리)
        Map<OrderStatus, Long> marketOrderStatusResponses = marketOrderService.findMarketOrderStatus(marketId);

        model.addAttribute("marketOrderStatus", marketOrderStatusResponses);

        return "market/marketOrder";
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') ")
    @GetMapping("/{marketId}/order-status/count")
    @ResponseBody
    public Map<OrderStatus, Long> getOrderStatusCount(@PathVariable("marketId") Long marketId) {
        // TODO : 해당 경로로 접근하는 member가 해당 market의 주인인지 판별할 것(예외처리)
        return marketOrderService.findMarketOrderStatus(marketId);
    }

    @GetMapping("/{marketId}/orders")
    @ResponseBody
    public List<MarketOrdersResponse> marketOrders(@PathVariable("marketId") Long marketId, @ModelAttribute MarketOrderSearchRequest searchRequest) {
        // TODO : 해당 경로로 접근하는 member가 해당 market의 주인인지 판별할 것(예외처리)
        return marketOrderService.getOrdersByStatusCondition(marketId, searchRequest);
    }

    @GetMapping("/{marketId}/search-orders")
    @ResponseBody
    public List<MarketOrdersResponse> marketSearchOrders(@PathVariable("marketId") Long marketId, @ModelAttribute MarketOrderSearchRequest searchRequest) {
        return marketOrderService.findAllOrdersWithSearch(marketId, searchRequest);
    }

    @GetMapping("/order/{orderId}")
    @ResponseBody
    public DetailMarketOrderResponse getOrderDetail(@PathVariable("orderId") Long orderId) {
        // TODO : 해당 경로로 접근하는 member가 해당 market의 주인인지 판별할 것(예외처리)
        log.info("orderId : {}", orderId);

        return marketOrderService.findDetailMarketOrder(orderId);
    }

    @GetMapping("/order/option/{orderId}")
    @ResponseBody
    public DetailMarketOrderResponseV2 getOrderDetailWithOptionCategory(@PathVariable("orderId") Long orderId) {
        // TODO : 가게 주문 처리 페이지에서 옵션 카테고리가 필요없을 경우 제거할 것
        log.info("orderId : {}", orderId);

        return marketOrderService.findDetailMarketOrderWithOption(orderId);
    }

}
