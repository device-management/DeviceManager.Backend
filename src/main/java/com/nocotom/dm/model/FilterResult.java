package com.nocotom.dm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FilterResult {

    @NotNull
    private final Collection<Device> devices;

    private final long total;
}
