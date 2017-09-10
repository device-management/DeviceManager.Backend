package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
@EqualsAndHashCode
public class DeviceRegisterEvent {

    @NotNull
    @Size(min=2, max=30)
    @Id
    private String deviceId;

    @NotNull
    private Map<String, Object> properties;
}
