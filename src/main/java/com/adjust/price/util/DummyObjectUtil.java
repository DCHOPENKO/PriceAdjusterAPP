package com.adjust.price.util;

import com.adjust.price.model.Price;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DummyObjectUtil {
    public static final Price DUMMY_PRICE = Price.builder()
            .id(-1L)
            .productCode("dummy")
            .begin(LocalDateTime.now())
            .end(LocalDateTime.now()).build();
}
