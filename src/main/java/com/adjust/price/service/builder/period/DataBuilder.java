package com.adjust.price.service.builder.period;

import com.adjust.price.model.Price;
import com.adjust.price.model.TimePeriod;

public interface DataBuilder {

    public TimePeriod convertFrom(Price price);
}
