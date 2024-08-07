package com.back.takeeat.dto.market.request;

import com.back.takeeat.domain.option.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketOptionRequest {

    private String optionName;
    private int optionPrice;

    public Option toOption() {
        return Option.builder()
                .optionName(optionName)
                .optionPrice(optionPrice)
                .build();
    }
}
