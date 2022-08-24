package com.adjust.price.service.builder.period.impl;

import com.adjust.price.model.Price;
import com.adjust.price.model.TimePeriod;
import com.adjust.price.service.builder.period.DataBuilder;
import org.springframework.stereotype.Service;

@Service
public class TimePeriodBuilder implements DataBuilder {

    public TimePeriod convertFrom(Price price) {
        return TimePeriod.builder()
                .begin(price.getBegin())
                .end(price.getEnd())
                .build();
    }
}
