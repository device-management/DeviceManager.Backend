package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@EqualsAndHashCode
public class DeviceStateChangedEvent {

    @NotNull
    @Size(min=2, max=30)
    private String deviceId;

    @NotNull
    private Map<String, Object> properties;
}
