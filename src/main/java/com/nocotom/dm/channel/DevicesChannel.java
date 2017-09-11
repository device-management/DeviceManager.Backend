package com.nocotom.dm.channel;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class DevicesChannel {
/*
    private final SimpMessagingTemplate messagingTemplate;

    public DevicesChannel(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/devices/{deviceId}/command")
    public void command(@DestinationVariable("deviceId") String deviceId, Map<String, Object> properties){
    }

    public void stateChanged(String deviceId, Map<String, Object> properties){
        messagingTemplate.convertAndSend(String.format("/devices/%s/state", deviceId), properties);
    }

    public void measurementOccurred(String deviceId){
        messagingTemplate.convertAndSend(String.format("/devices/%s/measurement", deviceId));
    }

    public void registered(String deviceId, Map<String, Object> properties){
        messagingTemplate.convertAndSend(String.format("/devices/%s/register", deviceId), properties);
    }*/
}
