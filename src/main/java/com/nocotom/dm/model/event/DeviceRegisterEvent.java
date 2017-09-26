package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceRegisterEvent extends DeviceEvent {

    @NotNull
    @Size(min=2, max=30)
    private String name;

    @NotNull
    @Size(min=2, max=30)
    private String type;

    @NotNull
    private Map<String, Object> configuration;

    @NotNull
    private Map<String, Object> state;

}
