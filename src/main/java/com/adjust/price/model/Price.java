package com.adjust.price.model;


import com.adjust.price.annotation.CsvColumn;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode (exclude = "id")
@Table(name = "price", catalog = "shop", schema = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CsvColumn(name = "код товара", col = 1)
    private String productCode;

    @CsvColumn(name = "номер цены", col = 2)
    private int number;

    @CsvColumn(name = "номер отдела", col = 3)
    private int depart;

    @CsvColumn(name = "начало действия", col = 4)
    @Column(name = "begin_date")
    private LocalDateTime begin;

    @CsvColumn(name = "конец действия", col = 5)
    @Column(name = "end_date")
    private LocalDateTime end;

    @CsvColumn(name = "значение цены в копейках", col = 6)
    long value;
}
