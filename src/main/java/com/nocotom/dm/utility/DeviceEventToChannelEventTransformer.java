package com.nocotom.dm.utility;

import com.nocotom.dm.model.event.ChannelEvent;
import com.nocotom.dm.model.event.DeviceEvent;
import org.springframework.integration.transformer.GenericTransformer;

public class DeviceEventToChannelEventTransformer implements GenericTransformer<DeviceEvent, ChannelEvent> {

    private final String eventName;

    public DeviceEventToChannelEventTransformer(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public ChannelEvent transform(DeviceEvent source) {
        return new ChannelEvent(String.format("/devices/%s", source.getDeviceId()), eventName, source);
    }
}
