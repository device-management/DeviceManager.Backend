package com.nocotom.dm.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelEvent {

    private final String channelName;

    private final String eventName;

    private final Object data;
}
