package com.adjust.price.service.predicate.impl;

import com.adjust.price.model.Price;
import com.adjust.price.model.TimePeriod;
import com.adjust.price.service.builder.period.DataBuilder;
import com.adjust.price.service.predicate.PredicateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.adjust.price.util.DummyObjectUtil.DUMMY_PRICE;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredicateServiceImpl implements PredicateService {

    private final DataBuilder periodService;

    public boolean checkIfPeriodTheSame(Price oldPrice, Price newPrice) {
        TimePeriod oldPeriod = periodService.convertFrom(oldPrice);
        TimePeriod newPeriod = periodService.convertFrom(newPrice);

        return oldPeriod.equals(newPeriod);
    }

    public boolean checkIfPeriodBeforeAll(List<Price> oldPrices, Price newPrice) {
        Price firstPrice = oldPrices
                .stream()
                .min(Comparator.comparing(Price::getBegin))
                .orElse(DUMMY_PRICE);

        return !firstPrice.equals(DUMMY_PRICE) &&
                (newPrice.getEnd().isBefore(firstPrice.getBegin()) ||
                        newPrice.getEnd().isEqual(firstPrice.getBegin()));
    }

    public boolean checkIfPeriodAfterAll(List<Price> oldPrices, Price newPrice) {
        Price lastPrice = oldPrices
                .stream()
                .max(Comparator.comparing(Price::getEnd))
                .orElse(DUMMY_PRICE);

        return !lastPrice.equals(DUMMY_PRICE) &&
                (newPrice.getBegin().isAfter(lastPrice.getEnd()) ||
                        newPrice.getBegin().isEqual(lastPrice.getEnd()));
    }

    public boolean checkIfPeriodBetweenExisting(List<Price> oldPrices, Price newPrice) {
        AtomicInteger counter = new AtomicInteger(0);

        long newBegin = convertToSeconds(newPrice.getBegin());
        long newEnd = convertToSeconds(newPrice.getEnd());

        oldPrices.forEach(oldPrice -> {
            long oldBegin = convertToSeconds(oldPrice.getBegin());
            long oldEnd = convertToSeconds(oldPrice.getEnd());
            if (newBegin >= oldEnd || (newBegin <= oldBegin && newEnd <= oldBegin)) {
                counter.incrementAndGet();
            }
        });

        return (counter.get() == oldPrices.size());
    }

    public boolean checkIfPeriodInsideExisting(Price oldPrice, Price newPrice) {

        long newBegin = convertToSeconds(newPrice.getBegin());
        long newEnd = convertToSeconds(newPrice.getEnd());
        long oldBegin = convertToSeconds(oldPrice.getBegin());
        long oldEnd = convertToSeconds(oldPrice.getEnd());
        return newBegin >= oldBegin && newEnd <= oldEnd;
    }

    public boolean checkIfNewBeginDateGreaterOrEqualExistBeginDate(Price oldPrice, Price newPrice) {
        long newBegin = convertToSeconds(newPrice.getBegin());
        long oldBegin = convertToSeconds(oldPrice.getBegin());
        return newBegin >= oldBegin;
    }

    public boolean checkIfNewEndDateGreaterOrEqualExistBeginDate(Price oldPrice, Price newPrice) {
        long newEnd = convertToSeconds(newPrice.getEnd());
        long oldBegin = convertToSeconds(oldPrice.getBegin());
        return newEnd >= oldBegin;
    }

    public boolean checkIfPriceCrossWithLeftBorderTimeline(Price leftPrice, Price rightPrice) {
        return !rightPrice.equals(DUMMY_PRICE) && leftPrice.equals(DUMMY_PRICE);
    }

    public boolean checkIfPriceCrossWithRightBorderTimeline(Price leftPrice, Price rightPrice) {
        return rightPrice.equals(DUMMY_PRICE) && !leftPrice.equals(DUMMY_PRICE);
    }

    public boolean checkIfPriceCrossWithSeveralTimelines(Price leftPrice, Price rightPrice) {
        return !rightPrice.equals(DUMMY_PRICE) && !leftPrice.equals(DUMMY_PRICE);
    }

    private long convertToSeconds(LocalDateTime ldt) {
        return ldt.toInstant(ZoneOffset.UTC).getEpochSecond();
    }

}
