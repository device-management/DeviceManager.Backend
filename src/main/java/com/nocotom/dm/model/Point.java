package com.nocotom.dm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
@ToString
public class Point {

    @NotNull
    private BigDecimal value;

    @NotNull
    private Instant timestamp;
}
