package com.nocotom.dm.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChannelEvent {

    @NotNull
    @Size(min=2, max=30)
    private String eventType;

    @NotNull
    private Object data;

    public static class Type {

        public static final String MEASUREMENT_OCCURRED = "event.device.measurement";

        public static final String DEVICE_REGISTERED = "event.device.register";

        public static final String DEVICE_UPDATED = "event.device.update";

        public static final String DEVICE_COMMAND = "event.device.command";
    }
}
