package com.adjust.price.util;

import com.adjust.price.model.Price;
import com.adjust.price.model.TimePeriod;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.adjust.price.util.DummyObjectUtil.DUMMY_PRICE;

@UtilityClass
public class PriceUtil {

    public static Price copyOf (Price price) {
        return Price.builder()
                .id(price.getId())
                .productCode(price.getProductCode())
                .number(price.getNumber())
                .depart(price.getDepart())
                .begin(price.getBegin())
                .end(price.getEnd())
                .value(price.getValue())
                .build();
    }

    public static String getUniqueCustomKey (Price price) {
        return String.join("",
                price.getProductCode(), String.valueOf(price.getNumber()), String.valueOf(price.getDepart()));
    }

    public static TimePeriod getTimerPeriod (Price price) {
        return TimePeriod.builder().begin(price.getBegin()).end(price.getEnd()).build();
    }

    public static Price getLeftBorderPrice (Collection<Price> prices) {
        return copyOf(prices.stream()
                .min(Comparator.comparing(Price::getBegin))
                .orElse(DUMMY_PRICE));
    }

    public static Price getRightBorderPrice (Collection<Price> prices) {
        return copyOf(prices.stream()
                .max(Comparator.comparing(Price::getEnd))
                .orElse(DUMMY_PRICE));
    }

}
