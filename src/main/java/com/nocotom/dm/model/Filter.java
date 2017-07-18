package com.nocotom.dm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@EqualsAndHashCode
public class Filter {

    @NotNull
    private Collection<FilterItem> filters;

    @NotNull
    private FilteringLogic logic;

    @Min(value = 0)
    private Integer limit;

    @Min(value = 0)
    private Integer offset;
}
