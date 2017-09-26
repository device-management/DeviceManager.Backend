package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceEvent;
import com.nocotom.dm.utility.Classes;
import org.springframework.integration.dsl.EnricherSpec;
import org.springframework.messaging.MessagingException;

public class DeviceHeaders {

    private DeviceHeaders(){
    }

    public static final String DEVICE_ID = "DeviceId";

    public static void addDeviceIdHeader(EnricherSpec enricherSpec) {
        enricherSpec.headerFunction(DEVICE_ID, message -> {
            DeviceEvent deviceEvent =
                    Classes.tryCast(message.getPayload(), DeviceEvent.class)
                            .orElseThrow(() -> new MessagingException("The payload is not a DeviceEvent instance."));
            return deviceEvent.getDeviceId();
        });
    }
}
