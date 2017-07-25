package com.nocotom.dm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@EqualsAndHashCode
public class FilterResult {

    @NotNull
    private final Collection<Device> devices;

    private final long total;
}
