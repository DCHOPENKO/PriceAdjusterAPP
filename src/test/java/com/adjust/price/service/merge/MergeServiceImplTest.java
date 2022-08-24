//package com.adjust.price.service.MergeService;
//
//import com.adjust.price.util.TestDataUtil;
//import com.adjust.price.model.Price;
//import com.adjust.price.model.TimePeriod;
//import com.adjust.price.service.TimePeriodService.TimePeriodService;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class MergeServiceImplTest {
//
//    @Autowired
//    private MergeServiceImpl  mergeService;
//    @Autowired
//    private TimePeriodService periodService;
//
//    private Map<TimePeriod, Price> periods;
//
//    @BeforeEach
//    void initTestSourceData() {
//        periods = TestDataUtil.generateTestSourceData();
//    }
//
//    @Test
//    void mergeIfPeriodTheSame_ShouldNotToChangeAnyDataInPeriods_PriceWithSamePeriodAndPrice() {
//
//        Price price = TestDataUtil.createPriceWithSamePeriodAndPrice();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodTheSame(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//        long valueExp = priceFromPeriods.getValue();
//        long idExp = priceFromPeriods.getId();
//
//        assertAll(
//                () -> assertThat(price).isNotEqualTo(priceFromPeriods),
//                () -> assertThat(price.getId()).isNotEqualTo(idExp),
//                () -> assertThat(price.getValue()).isEqualTo(valueExp),
//                () -> assertThat(result).isTrue(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//
//
//    }
//
//    @Test
//    void mergeIfPeriodTheSame_ShouldUpdateDataInPeriods_PriceWithSamePeriod() {
//
//        Price price = TestDataUtil.createPriceWithSamePeriod();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceBefore = periods.get(period);
//        long valueBefore = priceBefore.getValue();
//        long idBefore = priceBefore.getId();
//
//        boolean result = mergeService.mergeIfPeriodTheSame(periods, price);
//
//        Price priceAfter = periods.get(period);
//        long valueAfter = priceAfter.getValue();
//        long idAfter = priceAfter.getId();
//
//        assertAll(
//                () -> assertThat(priceBefore).isNotEqualTo(priceAfter),
//                () -> assertThat(idBefore).isEqualTo(idAfter),
//                () -> assertThat(valueBefore).isNotEqualTo(valueAfter),
//                () -> assertThat(result).isTrue(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodTheSame_ShouldNotChangeDataInPeriods_PriceWithNotSamePeriod() {
//
//        Price price = TestDataUtil.createPriceWithDifferenceInOneDate();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodTheSame(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//
//        assertAll(
//                () -> assertThat(priceFromPeriods).isNull(),
//                () -> assertThat(result).isFalse(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodBeforeAll_ShouldAddNewPriceToPeriods_PriceWithBeforeAllPeriod() {
//        Price price = TestDataUtil.createPriceWithBeforeAllPeriod();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodBeforeAll(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//        long valueExp = priceFromPeriods.getValue();
//        long idExp = priceFromPeriods.getId();
//
//        assertAll(
//                () -> assertThat(price).isEqualTo(priceFromPeriods),
//                () -> assertThat(price.getId()).isEqualTo(idExp),
//                () -> assertThat(price.getValue()).isEqualTo(valueExp),
//                () -> assertThat(result).isTrue(),
//                () -> assertThat(6).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodBeforeAll_ShouldNotAddNewPriceToPeriods_PriceWithCrossingPeriod() {
//        Price price = TestDataUtil.createPriceWithDatesCrossExistPeriod();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodBeforeAll(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//
//        assertAll(
//                () -> assertThat(priceFromPeriods).isNull(),
//                () -> assertThat(result).isFalse(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodBeforeAll_ShouldAddNewPriceToPeriods_PriceWithAfterAllPeriod() {
//        Price price = TestDataUtil.createPriceWithAfterAllPeriod();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodBeforeAll(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//
//        assertAll(
//                () -> assertThat(priceFromPeriods).isNull(),
//                () -> assertThat(result).isFalse(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodBeforeAll_ShouldNotAddNewPriceToPeriods_PriceWithSamrPeriod() {
//        Price price = TestDataUtil.createPriceWithAfterAllPeriod();
//
//        assertThat(5).isEqualTo(periods.size());
//
//        boolean result = mergeService.mergeIfPeriodBeforeAll(periods, price);
//
//        TimePeriod period = periodService.convertFrom(price);
//        Price priceFromPeriods = periods.get(period);
//
//        assertAll(
//                () -> assertThat(priceFromPeriods).isNull(),
//                () -> assertThat(result).isFalse(),
//                () -> assertThat(5).isEqualTo(periods.size())
//        );
//    }
//
//    @Test
//    void mergeIfPeriodAfterAll() {
//    }
//
//    @Test
//    void mergeIfPeriodBetweenExisting() {
//    }
//
//    @Test
//    void mergeIfPeriodInsideExisting() {
//    }
//
//    @Test
//    void mergeIfPeriodCrossWithAnother() {
//    }
//}