package com.adjust.price.service.predicate;

import com.adjust.price.model.Price;

import java.util.List;

public interface PredicateService {

    boolean checkIfPeriodTheSame(Price oldPrice, Price newPrice);

    boolean checkIfPeriodBeforeAll(List<Price> oldPrices, Price newPrice);

    boolean checkIfPeriodAfterAll(List<Price> oldPrices, Price newPrice);

    boolean checkIfPeriodBetweenExisting(List<Price> oldPrices, Price newPrice);

    boolean checkIfPeriodInsideExisting(Price oldPrice, Price newPrice);

    boolean checkIfNewBeginDateGreaterOrEqualExistBeginDate(Price oldPrice, Price newPrice);

    boolean checkIfNewEndDateGreaterOrEqualExistBeginDate(Price oldPrice, Price newPrice);

    boolean checkIfPriceCrossWithLeftBorderTimeline(Price leftPrice, Price rightPrice);

    boolean checkIfPriceCrossWithRightBorderTimeline(Price leftPrice, Price rightPrice);

    boolean checkIfPriceCrossWithSeveralTimelines(Price leftPrice, Price rightPrice);
}
