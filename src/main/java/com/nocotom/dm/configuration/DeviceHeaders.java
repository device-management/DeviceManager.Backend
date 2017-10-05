package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceEvent;
import org.springframework.integration.dsl.EnricherSpec;

class DeviceHeaders {

    private DeviceHeaders(){
    }

    static final String DEVICE_ID = "DeviceId";

    static void addDeviceIdHeader(EnricherSpec enricherSpec) {
        enricherSpec.<DeviceEvent>headerFunction(DEVICE_ID, message -> message.getPayload().getDeviceId());
    }
}
