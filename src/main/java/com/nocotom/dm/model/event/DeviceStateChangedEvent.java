package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceStateChangedEvent extends DeviceEvent {

    @NotNull
    private Map<String, Object> state;
}
