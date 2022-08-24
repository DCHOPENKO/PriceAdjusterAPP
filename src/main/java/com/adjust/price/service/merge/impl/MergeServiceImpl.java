package com.adjust.price.service.merge.impl;


import com.adjust.price.model.Price;
import com.adjust.price.service.merge.MergeService;
import com.adjust.price.service.predicate.PredicateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.adjust.price.util.DummyObjectUtil.DUMMY_PRICE;

@Service
@RequiredArgsConstructor
public class MergeServiceImpl implements MergeService {

    private final PredicateService predicateService;

    public Collection<Price> merge(List<Price> oldPrices, List<Price> newPrices) {
        Map<String, List<Price>> existData = groupPricesByUniqueParams(oldPrices);
        Map<String, List<Price>> newData = groupPricesByUniqueParams(newPrices);

        Set<Price> mergedResultData = getDataWhichNoNeedMergeFromBothLists(existData, newData);

        newData.entrySet().stream()
                .filter(it -> existData.containsKey(it.getKey()))
                .forEach(it -> it.getValue().forEach(newPrice -> {
                    List<Price> existPrices = existData.get(it.getKey());
                    List<Price> merged = new ArrayList<>();

                    boolean isTrue = updateMergedDataWhichNotCrossWithExisting(merged, newPrice, existPrices);
                    if (!isTrue) {
                        updateMergedDataWhichCrossWithExistPeriods(merged, newPrice);
                    }

                    mergedResultData.addAll(merged);
                }));

        return mergedResultData;
    }

    private void updateMergedDataWhichCrossWithExistPeriods(List<Price> mergedList, Price newPrice) {
        AtomicReference<Price> aLeftPrice = new AtomicReference<>();
        AtomicBoolean aIsRightPrice = new AtomicBoolean(false);
        AtomicBoolean aIsLeftPrice = new AtomicBoolean(false);

        List<Price> toDelete = new ArrayList<>();

        mergedList.forEach((oldPrice -> {
            if (predicateService.checkIfNewBeginDateGreaterOrEqualExistBeginDate(oldPrice, newPrice)) {
                aLeftPrice.set(oldPrice);
                aIsLeftPrice.set(true);
            } else {
                if (predicateService.checkIfNewEndDateGreaterOrEqualExistBeginDate(oldPrice, newPrice)) {
                    toDelete.add(oldPrice);
                    aIsRightPrice.set(true);
                }
            }
        }));

        Price leftPrice = aIsLeftPrice.get() ? aLeftPrice.get() : DUMMY_PRICE;
        Price rightPrice = aIsRightPrice.get() ? toDelete.remove(toDelete.size() - 1) : DUMMY_PRICE;

        mergedList.removeAll(toDelete);

        mergeIfCrossWithLeftBorder(mergedList, newPrice, leftPrice, rightPrice);
        mergeIfCrossWithRightBorder(mergedList, newPrice, leftPrice, rightPrice);
        mergeIfCrossWithSeveralTimelines(mergedList, newPrice, leftPrice, rightPrice);
    }

    private void mergeIfCrossWithSeveralTimelines(List<Price> mergedList, Price newPrice, Price leftPrice, Price rightPrice) {
        if (predicateService.checkIfPriceCrossWithSeveralTimelines(leftPrice, rightPrice) &&
                newPrice.getValue() == leftPrice.getValue() && newPrice.getValue() == rightPrice.getValue()) {

            leftPrice.setEnd(rightPrice.getEnd());
            mergedList.remove(rightPrice);
            mergedList.add(leftPrice);
        } else if (predicateService.checkIfPriceCrossWithSeveralTimelines(leftPrice, rightPrice) &&
                newPrice.getValue() == leftPrice.getValue() && newPrice.getValue() != rightPrice.getValue()) {

            leftPrice.setEnd(newPrice.getEnd());
            rightPrice.setBegin(newPrice.getEnd());

            mergedList.addAll(List.of(leftPrice, rightPrice));

        } else if (predicateService.checkIfPriceCrossWithSeveralTimelines(leftPrice, rightPrice) &&
                newPrice.getValue() != leftPrice.getValue() && newPrice.getValue() == rightPrice.getValue()) {

            rightPrice.setBegin(newPrice.getBegin());
            leftPrice.setEnd(newPrice.getBegin());

            mergedList.addAll(List.of(leftPrice, rightPrice));
        } else if (predicateService.checkIfPriceCrossWithSeveralTimelines(leftPrice, rightPrice)) {

            leftPrice.setEnd(newPrice.getBegin());
            rightPrice.setBegin(newPrice.getEnd());
            mergedList.addAll(List.of(leftPrice, newPrice, rightPrice));
        }
    }

    private void mergeIfCrossWithRightBorder(List<Price> mergedList, Price newPrice, Price leftPrice, Price rightPrice) {
        if (predicateService.checkIfPriceCrossWithRightBorderTimeline(leftPrice, rightPrice) &&
                newPrice.getValue() == leftPrice.getValue()) {
            leftPrice.setEnd(newPrice.getEnd());
            mergedList.add(leftPrice);
        } else if (predicateService.checkIfPriceCrossWithRightBorderTimeline(leftPrice, rightPrice)) {
            leftPrice.setEnd(newPrice.getBegin());
            mergedList.addAll(List.of(leftPrice, newPrice));
        }
    }

    private void mergeIfCrossWithLeftBorder(List<Price> mergedList, Price newPrice, Price leftPrice, Price rightPrice) {
        if (predicateService.checkIfPriceCrossWithLeftBorderTimeline(leftPrice, rightPrice) &&
                newPrice.getValue() == rightPrice.getValue()) {
            rightPrice.setBegin(newPrice.getBegin());
            mergedList.add(rightPrice);
        } else if (predicateService.checkIfPriceCrossWithLeftBorderTimeline(leftPrice, rightPrice)) {
            rightPrice.setBegin(newPrice.getEnd());
            mergedList.addAll(List.of(rightPrice, newPrice));
        }
    }

    private boolean updateMergedDataWhichNotCrossWithExisting(List<Price> mergedData, Price newPrice, List<Price> existPrices) {
        AtomicBoolean aIsTrue = new AtomicBoolean(false);
        if (predicateService.checkIfPeriodBeforeAll(existPrices, newPrice) ||
                predicateService.checkIfPeriodAfterAll(existPrices, newPrice) ||
                predicateService.checkIfPeriodBetweenExisting(existPrices, newPrice)) {
            mergedData.add(newPrice);
            aIsTrue.set(true);
        }

        existPrices.forEach(oldPrice -> {
            if (predicateService.checkIfPeriodTheSame(oldPrice, newPrice)) {
                if (newPrice.getValue() != oldPrice.getValue()) {
                    oldPrice.setValue(newPrice.getValue());
                }
                    mergedData.add(oldPrice);
                    aIsTrue.set(true);
            } else if (predicateService.checkIfPeriodInsideExisting(oldPrice, newPrice)) {
                if (newPrice.getValue() == oldPrice.getValue()) {
                    mergedData.add(oldPrice);
                    aIsTrue.set(true);
                } else {
                    Price price = Price.builder()
                            .productCode(oldPrice.getProductCode())
                            .number(oldPrice.getNumber())
                            .depart(oldPrice.getDepart())
                            .begin(newPrice.getEnd())
                            .end(oldPrice.getEnd())
                            .value(oldPrice.getValue())
                            .build();
                    oldPrice.setEnd(newPrice.getBegin());
                    mergedData.addAll(List.of(oldPrice, newPrice, price));
                    aIsTrue.set(true);
                }
            } else {
                mergedData.add(oldPrice);
            }
        });
        return aIsTrue.get();
    }


    private Set<Price> getDataWhichNoNeedMergeFromBothLists(Map<String, List<Price>> oldData,
                                                            Map<String, List<Price>> newData) {
        Set<Price> data = getDataWhichNoNeedMergeFromFirstList(oldData, newData);
        data.addAll(getDataWhichNoNeedMergeFromFirstList(newData, oldData));
        return data;
    }

    private Set<Price> getDataWhichNoNeedMergeFromFirstList(Map<String, List<Price>> firstList,
                                                            Map<String, List<Price>> secondList) {
        Set<Price> data = new LinkedHashSet<>();
        secondList.keySet().forEach(key -> {
            if (!firstList.containsKey(key)) {
                data.addAll(secondList.get(key));
            }
        });
        return data;
    }

    private Map<String, List<Price>> groupPricesByUniqueParams(List<Price> data) {
        Map<String, List<Price>> result = new HashMap<>();

        data.stream()
                .sorted(Comparator.comparing(Price::getBegin))
                .forEach(it -> {
                    String key = String.join("",
                            it.getProductCode(), String.valueOf(it.getNumber()), String.valueOf(it.getDepart()));
                    if (result.containsKey(key)) {
                        result.get(key).add(it);
                    } else {
                        result.put(key, new ArrayList<>(List.of(it)));
                    }
                });
        return result;
    }
}
