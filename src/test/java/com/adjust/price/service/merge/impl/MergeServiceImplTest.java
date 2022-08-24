package com.adjust.price.service.merge.impl;

import com.adjust.price.model.TimePeriod;
import com.adjust.price.util.PriceTestDataUtil;
import com.adjust.price.model.Price;
import com.adjust.price.util.PriceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static com.adjust.price.util.DummyObjectUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MergeServiceImplTest {

    @Autowired
    private MergeServiceImpl mergeService;

    private List<Price> existPrices;

    @BeforeEach
    void init() {
        existPrices = PriceTestDataUtil.generateTestSourceData();
    }


    @Nested
    class PositiveCases {

        @Nested
        class ShouldUpdateExistDataWithTheSamePeriod {

            @Test
            void mergeDataWithTheSamePeriodAndPrice() {
                Price newPrice = PriceTestDataUtil.createPriceWithSamePeriodAndPrice();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = PriceUtil.copyOf(existPrices.get(0));
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod).isEqualTo(actTimePeriod),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actPrice.getValue()),
                        () -> assertThat(newPrice.getId()).isNotEqualTo(actPrice.getId())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));

                Price expPrice = merged.stream().filter(it -> it.equals(newPrice)).findFirst().orElse(DUMMY_PRICE);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(newPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(actTimePeriod).isEqualTo(expTimePeriod),
                        () -> assertThat(newPrice.getId()).isNotEqualTo(expPrice.getId())
                );
            }

            @Test
            void mergeDataWithTheSamePeriod() {
                Price newPrice = PriceTestDataUtil.createPriceWithSamePeriod();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = PriceUtil.copyOf(existPrices.get(0));
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod).isEqualTo(actTimePeriod),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actPrice.getValue()),
                        () -> assertThat(newPrice.getId()).isNotEqualTo(actPrice.getId())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));

                Price expPrice = merged.stream().filter(it -> it.equals(newPrice)).findFirst().orElse(DUMMY_PRICE);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(newPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(actTimePeriod).isEqualTo(expTimePeriod),
                        () -> assertThat(actPrice.getId()).isEqualTo(expPrice.getId()),
                        () -> assertThat(actPrice.getValue()).isNotEqualTo(expPrice.getValue()),
                        () -> assertThat(newPrice.getId()).isNotEqualTo(expPrice.getId())
                );
            }
        }

        @Nested
        class ShouldAddDataBeforeLeftTimeline {
            @Test
            void mergeDataWithBeforeLeftPeriod() {
                Price newPrice = PriceTestDataUtil.createPriceWithBeforeAllPeriod();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = PriceUtil.getLeftBorderPrice(existPrices);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod).isNotEqualTo(actTimePeriod),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actTimePeriod.getBegin())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));

                Price expPrice = PriceUtil.getLeftBorderPrice(merged);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actTimePeriod.getBegin()).isAfterOrEqualTo(expTimePeriod.getEnd()),
                        () -> assertThat(newPrice).isEqualTo(expPrice),
                        () -> assertThat(actTimePeriod).isNotEqualTo(expTimePeriod),
                        () -> assertThat(actPrice).isNotEqualTo(expPrice)
                );
            }
        }

        @Nested
        class ShouldAddDataAfterLeftTimeline {
            @Test
            void mergeDataWithAfterRightPeriod() {
                Price newPrice = PriceTestDataUtil.createPriceWithAfterAllPeriod();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = PriceUtil.getRightBorderPrice(existPrices);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod).isNotEqualTo(actTimePeriod),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actTimePeriod.getEnd())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));

                Price expPrice = PriceUtil.getRightBorderPrice(merged);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actTimePeriod.getEnd()).isBeforeOrEqualTo(expTimePeriod.getBegin()),
                        () -> assertThat(newPrice).isEqualTo(expPrice),
                        () -> assertThat(actTimePeriod).isNotEqualTo(expTimePeriod),
                        () -> assertThat(actPrice).isNotEqualTo(expPrice)
                );
            }

        }

        @Nested
        class ShouldAddDataBetweenExistingPeriod {

            @Test
            void mergeDataWithTimelineBetweenExisting() {
                Price newPrice = PriceTestDataUtil.createPriceWithMiddleEmptyPeriod();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actLeftPrice = existPrices.get(3);
                String actLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(actLeftPrice);
                TimePeriod actLeftTimePeriod = PriceUtil.getTimerPeriod(actLeftPrice);

                Price actRightPrice = existPrices.get(4);
                String actRightUniquePriceKey = PriceUtil.getUniqueCustomKey(actRightPrice);
                TimePeriod actRightTimePeriod = PriceUtil.getTimerPeriod(actRightPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actLeftUniquePriceKey),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actRightUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actLeftTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actRightTimePeriod.getBegin())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(3);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);

                Price expMiddlePrice = sortedData.get(4);
                TimePeriod expMiddlePriceTimePeriod = PriceUtil.getTimerPeriod(expMiddlePrice);

                Price expRightPrice = sortedData.get(5);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actRightUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actLeftTimePeriod.getEnd()).isBeforeOrEqualTo(expMiddlePriceTimePeriod.getBegin()),
                        () -> assertThat(actRightTimePeriod.getBegin()).isAfterOrEqualTo(expMiddlePriceTimePeriod.getEnd()),
                        () -> assertThat(actLeftPrice).isEqualTo(expLeftPrice),
                        () -> assertThat(actRightPrice).isEqualTo(expRightPrice),
                        () -> assertThat(newPrice).isEqualTo(expMiddlePrice)
                );
            }

        }

        @Nested
        class ShouldMergeDataWhenPeriodInside {

            @Test
            void mergeDataWhenPeriodInsideAndValueTheSame() {
                Price newPrice = PriceTestDataUtil.createPriceWithDatesInsideExistPeriodAndSameValue();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(0);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actPrice.getValue()),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actTimePeriod.getEnd())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expPrice = sortedData.get(0);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(actPrice).isEqualTo(expPrice),
                        () -> assertThat(actPrice.getId()).isEqualTo(expPrice.getId()),
                        () -> assertThat(merged.contains(newPrice)).isFalse()
                );
            }

            @Test
            void mergeDataWhenPeriodInside() {
                Price newPrice = PriceTestDataUtil.createPriceWithDatesInsideExistPeriod();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(0);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(0);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expMiddlePrice = sortedData.get(1);

                Price expRightPrice = sortedData.get(2);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);


                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(8),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(newPrice).isEqualTo(expMiddlePrice),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getEnd()),
                        () -> assertThat(actTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getBegin()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expRightPrice.getValue())
                );
            }
        }

        @Nested
        class ShouldMergeDataWhenPeriodCrossWithLeftBorder {

            @Test
            void mergeDataWhenPeriodCrossWithLeftBorderAndSameValue() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossLeftBorderAndSameValue();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(0);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isBefore(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedMerge = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expPrice = sortedMerge.get(0);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actTimePeriod.getEnd()).isEqualTo(expTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getBegin()).isEqualTo(expTimePeriod.getBegin()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(actPrice.getId()).isEqualTo(expPrice.getId()),
                        () -> assertThat(merged.contains(newPrice)).isFalse(),
                        () -> assertThat(newPrice).isNotEqualTo(expPrice)
                );
            }

            @Test
            void mergeDataWhenPeriodCrossWithLeftBorder() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossLeftBorder();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(0);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isBefore(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(0);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expRightPrice = sortedData.get(1);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getEnd()),
                        () -> assertThat(actTimePeriod.getBegin()).isBeforeOrEqualTo(expRightPriceTimePeriod.getBegin()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expRightPrice.getValue()),
                        () -> assertThat(actPrice.getId()).isEqualTo(expRightPrice.getId()),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expRightPrice.getValue()),
                        () -> assertThat(newPrice).isEqualTo(expLeftPrice),
                        () -> assertThat(actTimePeriod.getBegin()).isAfter(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(actPrice.getValue()).isNotEqualTo(expLeftPrice.getValue())
                );
            }

        }

        @Nested
        class ShouldMergeDataWhenPeriodCrossWithRightBorder {

            @Test
            void mergeDataWhenPeriodCrossWithRightBorderAndSameValue() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossRightBorderAndSameValue();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(existPrices.size() - 1);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfter(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isAfter(actTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getBegin()).isBeforeOrEqualTo(actTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedMerge = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expPrice = sortedMerge.get(sortedMerge.size() - 1);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actTimePeriod.getBegin()).isEqualTo(expTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isEqualTo(expTimePeriod.getEnd()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(actPrice.getId()).isEqualTo(expPrice.getId()),
                        () -> assertThat(merged.contains(newPrice)).isFalse(),
                        () -> assertThat(newPrice).isNotEqualTo(expPrice)
                );
            }

            @Test
            void mergeDataWhenPeriodCrossWithRightBorder() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossRightBorder();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actPrice = existPrices.get(existPrices.size() - 1);
                String actUniquePriceKey = PriceUtil.getUniqueCustomKey(actPrice);
                TimePeriod actTimePeriod = PriceUtil.getTimerPeriod(actPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfter(actTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isAfter(actTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getBegin()).isBeforeOrEqualTo(actTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(sortedData.size() - 2);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expRightPrice = sortedData.get(sortedData.size() - 1);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(actTimePeriod.getBegin()).isBefore(expRightPriceTimePeriod.getBegin()),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actPrice.getId()).isEqualTo(expLeftPrice.getId()),
                        () -> assertThat(merged.contains(newPrice)).isTrue(),
                        () -> assertThat(newPrice).isEqualTo(expRightPrice),
                        () -> assertThat(actPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actPrice.getValue()).isNotEqualTo(expRightPrice.getValue())
                );
            }


        }

        @Nested
        class ShouldMergeDataWhenPeriodCrossWithSeveralTimelines {

            @Test
            void mergeDataWhenPeriodCrossWithTwoExistingTimelinesAndSameValueFromLeft() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossTwoExistPeriodsAndLeftSamePrice();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actLeftPrice = existPrices.get(0);
                String actLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(actLeftPrice);
                TimePeriod actLeftTimePeriod = PriceUtil.getTimerPeriod(actLeftPrice);

                Price actRightPrice = existPrices.get(1);
                String actRightUniquePriceKey = PriceUtil.getUniqueCustomKey(actRightPrice);
                TimePeriod actRightTimePeriod = PriceUtil.getTimerPeriod(actRightPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actLeftUniquePriceKey),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actRightUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actLeftTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actRightTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actLeftPrice.getValue()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actRightPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(0);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expRightPrice = sortedData.get(1);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(actRightUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actLeftTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(actRightTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getEnd()).isEqualTo(expLeftPriceTimePeriod.getEnd()),
                        () -> assertThat(actLeftPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actRightPrice.getValue()).isEqualTo(expRightPrice.getValue()),
                        () -> assertThat(merged.contains(newPrice)).isFalse()
                );
            }


            @Test
            void mergeDataWhenPeriodCrossWithTwoExistingTimelinesAndSameValueFromRight() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossTwoExistPeriodsAndRightSamePrice();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actLeftPrice = existPrices.get(0);
                String actLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(actLeftPrice);
                TimePeriod actLeftTimePeriod = PriceUtil.getTimerPeriod(actLeftPrice);

                Price actRightPrice = existPrices.get(1);
                String actRightUniquePriceKey = PriceUtil.getUniqueCustomKey(actRightPrice);
                TimePeriod actRightTimePeriod = PriceUtil.getTimerPeriod(actRightPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actLeftUniquePriceKey),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actRightUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actLeftTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actRightTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actRightPrice.getValue()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actLeftPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(0);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expRightPrice = sortedData.get(1);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(6),
                        () -> assertThat(actRightUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actLeftTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(actRightTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getEnd()),
                        () -> assertThat(actLeftPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actRightPrice.getValue()).isEqualTo(expRightPrice.getValue()),
                        () -> assertThat(merged.contains(newPrice)).isFalse()
                );
            }


            @Test
            void mergeDataWhenPeriodCrossWithTwoExistingTimelinesAndSameValueFromBothSides() {
                Price newPrice = PriceTestDataUtil.createPriceWithCrossTwoExistPeriodsAndBothSamePrices();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actLeftPrice = existPrices.get(1);
                String actLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(actLeftPrice);
                TimePeriod actLeftTimePeriod = PriceUtil.getTimerPeriod(actLeftPrice);

                Price actRightPrice = existPrices.get(4);
                String actRightUniquePriceKey = PriceUtil.getUniqueCustomKey(actRightPrice);
                TimePeriod actRightTimePeriod = PriceUtil.getTimerPeriod(actRightPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actLeftUniquePriceKey),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actRightUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actLeftTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actRightTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actRightPrice.getValue()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(actLeftPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expPrice = sortedData.get(1);
                String expUniquePriceKey = PriceUtil.getUniqueCustomKey(expPrice);
                TimePeriod expPriceTimePeriod = PriceUtil.getTimerPeriod(expPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(3),
                        () -> assertThat(actRightUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expUniquePriceKey),
                        () -> assertThat(actLeftTimePeriod.getBegin()).isEqualTo(expPriceTimePeriod.getBegin()),
                        () -> assertThat(actRightTimePeriod.getEnd()).isEqualTo(expPriceTimePeriod.getEnd()),
                        () -> assertThat(actLeftPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(actRightPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(newPrice.getValue()).isEqualTo(expPrice.getValue()),
                        () -> assertThat(merged.contains(newPrice)).isFalse()
                );
            }

            @Test
            void mergeDataWhenPeriodCrossWithTwoExistingTimelines() {
                Price newPrice = PriceTestDataUtil.createPriceWithDatesCrossTwoExistPeriods();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                TimePeriod newTimePeriod = PriceUtil.getTimerPeriod(newPrice);

                Price actLeftPrice = existPrices.get(0);
                String actLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(actLeftPrice);
                TimePeriod actLeftTimePeriod = PriceUtil.getTimerPeriod(actLeftPrice);

                Price actRightPrice = existPrices.get(1);
                String actRightUniquePriceKey = PriceUtil.getUniqueCustomKey(actRightPrice);
                TimePeriod actRightTimePeriod = PriceUtil.getTimerPeriod(actRightPrice);

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actLeftUniquePriceKey),
                        () -> assertThat(newUniquePriceKey).isEqualTo(actRightUniquePriceKey),
                        () -> assertThat(newTimePeriod.getBegin()).isAfterOrEqualTo(actLeftTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getEnd()).isBeforeOrEqualTo(actRightTimePeriod.getEnd()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actRightPrice.getValue()),
                        () -> assertThat(newPrice.getValue()).isNotEqualTo(actLeftPrice.getValue())
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));
                List<Price> sortedData = merged.stream().sorted(Comparator.comparing(Price::getBegin)).toList();

                Price expLeftPrice = sortedData.get(0);
                String expLeftUniquePriceKey = PriceUtil.getUniqueCustomKey(expLeftPrice);
                TimePeriod expLeftPriceTimePeriod = PriceUtil.getTimerPeriod(expLeftPrice);

                Price expMiddlePrice = sortedData.get(1);
                String expMiddleUniquePriceKey = PriceUtil.getUniqueCustomKey(expMiddlePrice);
                TimePeriod expMiddlePriceTimePeriod = PriceUtil.getTimerPeriod(expMiddlePrice);

                Price expRightPrice = sortedData.get(2);
                String expRightUniquePriceKey = PriceUtil.getUniqueCustomKey(expRightPrice);
                TimePeriod expRightPriceTimePeriod = PriceUtil.getTimerPeriod(expRightPrice);

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(actRightUniquePriceKey).isEqualTo(expLeftUniquePriceKey),
                        () -> assertThat(actLeftUniquePriceKey).isEqualTo(expRightUniquePriceKey),
                        () -> assertThat(actLeftTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getBegin()),
                        () -> assertThat(newTimePeriod.getBegin()).isEqualTo(expLeftPriceTimePeriod.getEnd()),
                        () -> assertThat(newTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getBegin()),
                        () -> assertThat(actRightTimePeriod.getEnd()).isEqualTo(expRightPriceTimePeriod.getEnd()),
                        () -> assertThat(actLeftPrice.getValue()).isEqualTo(expLeftPrice.getValue()),
                        () -> assertThat(actRightPrice.getValue()).isEqualTo(expRightPrice.getValue()),
                        () -> assertThat(newPrice).isEqualTo(expMiddlePrice),
                        () -> assertThat(merged.contains(newPrice)).isTrue()
                );
            }
        }

        @Nested
        class ShouldAddDataWhenNoMatchingWithExistObjects {

            @Test
            void addDataWhenNoMatchingWithUniqueKey() {
                Price newPrice = PriceTestDataUtil.createPriceWithAnotherDepart();

                String newUniquePriceKey = PriceUtil.getUniqueCustomKey(newPrice);
                String maybeKey = existPrices.stream()
                        .map(PriceUtil::getUniqueCustomKey)
                        .filter(it -> it.equals(newUniquePriceKey))
                        .findFirst()
                        .orElse("other");

                assertAll(
                        () -> assertThat(existPrices.size()).isEqualTo(6),
                        () -> assertThat(newUniquePriceKey).isNotEqualTo(maybeKey),
                        () -> assertThat(maybeKey).isEqualTo("other")
                );

                Collection<Price> merged = mergeService.merge(existPrices, List.of(newPrice));

                assertAll(
                        () -> assertThat(merged.size()).isEqualTo(7),
                        () -> assertThat(merged.contains(newPrice)).isTrue()
                );
            }

        }

    }

}




