package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
public abstract class DeviceEvent {

    @NotNull
    @Size(min=2, max=30)
    private String deviceId;
}
