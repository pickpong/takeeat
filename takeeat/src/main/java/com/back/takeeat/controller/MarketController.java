package com.back.takeeat.controller;

import com.back.takeeat.domain.market.MarketInfoForm;
import com.back.takeeat.service.MarketInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketController {

    private final MarketInfoService marketInfoService;

    @GetMapping("/info")
    public String marketInfo() {
        return "/market/marketInfo";
    }

    @PostMapping("/save")
    public String saveInfo(@Valid MarketInfoForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "/market/marketInfo";
        }

        return "redirect:/menu";
    }
    @GetMapping("/menu")
    public String marketMenu() {
        return "/market/marketMenu";
    }
    @GetMapping("/option")
    public String marketOption() {
        return "/market/marketOption";
    }

    @GetMapping("/review")
    public String marketReview() {
        return "market/marketReview";
    }


}
