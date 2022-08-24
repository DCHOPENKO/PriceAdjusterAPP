package com.adjust.price.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimePeriod {

    private LocalDateTime begin;

    private LocalDateTime end;
}
