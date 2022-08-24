package com.adjust.price.util;

import com.adjust.price.model.Price;
import com.adjust.price.service.builder.period.impl.TimePeriodBuilder;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class PriceTestDataUtil {



    public static Price createPriceWithMiddleEmptyPeriod() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2022, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2022, 05, 05, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithAfterAllPeriod() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2050, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2051, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithBeforeAllPeriod() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2000, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2001, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithSamePeriodAndPrice() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2010, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2014, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithSamePeriod() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2010, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2014, 01, 01, 00, 00, 00))
                .value(700)
                .build();
    }

    public static Price createPriceWithDifferenceInOneDate() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2000, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2014, 01, 01, 00, 00, 00))
                .value(700)
                .build();
    }

    public static Price createPriceWithDatesInsideExistPeriod() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2011, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .value(700)
                .build();
    }

    public static Price createPriceWithDatesInsideExistPeriodAndSameValue() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2011, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithDatesCrossTwoExistPeriods() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .value(700)
                .build();
    }

    public static Price createPriceWithAnotherDepart() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(4)
                .begin(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .value(700)
                .build();
    }

    public static Price createPriceWithCrossTwoExistPeriodsAndLeftSamePrice() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithCrossTwoExistPeriodsAndRightSamePrice() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .value(200)
                .build();
    }

    public static Price createPriceWithCrossTwoExistPeriodsAndBothSamePrices() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2027, 01, 01, 00, 00, 00))
                .value(200)
                .build();
    }

    public static Price createPriceWithCrossTwoPlusExistPeriods() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2015, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2019, 01, 01, 00, 00, 00))
                .value(200)
                .build();
    }


    public static Price createPriceWithCrossLeftBorderAndSameValue() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2008, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .value(100)
                .build();
    }

    public static Price createPriceWithCrossLeftBorder() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2008, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2012, 01, 01, 00, 00, 00))
                .value(500)
                .build();
    }

    public static Price createPriceWithCrossRightBorder() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2033, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2040, 01, 01, 00, 00, 00))
                .value(500)
                .build();
    }

    public static Price createPriceWithCrossRightBorderAndSameValue() {
        return Price.builder()
                .id(800)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2033, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2040, 01, 01, 00, 00, 00))
                .value(333)
                .build();
    }

    public static List<Price> generateTestSourceData() {

        TimePeriodBuilder periodService = new TimePeriodBuilder();

        Price first = Price.builder()
                .id(1)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2010, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2014, 01, 01, 00, 00, 00))
                .value(100)
                .build();

        Price second = Price.builder()
                .id(2)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2014, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2016, 01, 01, 00, 00, 00))
                .value(200)
                .build();

        Price third = Price.builder()
                .id(3)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2016, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2018, 01, 01, 00, 00, 00))
                .value(500)
                .build();

        Price fourth = Price.builder()
                .id(5)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2018, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2020, 01, 01, 00, 00, 00))
                .value(777)
                .build();

        Price fifth = Price.builder()
                .id(4)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2025, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2030, 01, 01, 00, 00, 00))
                .value(200)
                .build();

        Price sixth = Price.builder()
                .id(5)
                .productCode("item_01")
                .number(1)
                .depart(1)
                .begin(LocalDateTime.of(2030, 01, 01, 00, 00, 00))
                .end(LocalDateTime.of(2035, 01, 01, 00, 00, 00))
                .value(333)
                .build();


        return List.of(first, second, third, fourth, fifth, sixth);
    }
}
