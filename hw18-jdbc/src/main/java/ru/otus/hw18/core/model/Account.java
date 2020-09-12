package ru.otus.hw18.core.model;

import lombok.*;
import ru.otus.hw18.core.annotation.Id;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    private long no;
    private String type;
    private BigDecimal rest;
}
