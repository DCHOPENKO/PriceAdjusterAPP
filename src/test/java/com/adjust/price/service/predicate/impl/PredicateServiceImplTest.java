package com.adjust.price.service.predicate.impl;

import com.adjust.price.util.PriceTestDataUtil;
import com.adjust.price.model.Price;
import com.adjust.price.service.builder.period.impl.TimePeriodBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PredicateServiceImplTest {
    private final static List<Price> EXIST_DATA = PriceTestDataUtil.generateTestSourceData();
    private final static Price EXIST_PRICE = PriceTestDataUtil.createPriceWithSamePeriodAndPrice();


    @Autowired
    private TimePeriodBuilder periodBuilder;

    @Autowired
    private PredicateServiceImpl predicateService;

    @Test
    void checkIfPeriodTheSame() {
    }

    @Test
    void checkIfPeriodBeforeAll() {
    }

    @Test
    void checkIfPeriodAfterAll() {
    }

    @Test
    void checkIfPeriodBetweenExisting() {
    }

    @Test
    void checkIfPeriodInsideExisting() {
    }

    @Test
    void checkIfNewBeginDateGreaterOrEqualExistBeginDate() {
    }

    @Test
    void checkIfNewEndDateGreaterOrEqualExistBeginDate() {
    }

    @Test
    void checkIfPriceCrossWithLeftBorderTimeline() {
    }

    @Test
    void checkIfPriceCrossWithRightBorderTimeline() {
    }

    @Test
    void checkIfPriceCrossWithSeveralTimelines() {
    }
}