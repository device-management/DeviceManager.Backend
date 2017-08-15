package com.nocotom.dm.controller;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.BroadcasterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path = "api/channels")
public class ChannelsController {

    private final BroadcasterFactory factory;

    public ChannelsController(BroadcasterFactory factory) {
        this.factory = factory;
    }

    @RequestMapping(value="{channelName}/subscribe")
    @ResponseStatus(HttpStatus.OK)
    public void subscribe(@RequestParam("channelName") String channelName, HttpServletRequest request){
        AtmosphereResource resource = (AtmosphereResource)request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        resource.suspend();
        factory.get(channelName).addAtmosphereResource(resource);
    }

    @RequestMapping(value="{channelName}/unsubscribe")
    public void unsubscribe(@RequestParam("channelName") String channelName, HttpServletRequest request){
        AtmosphereResource resource = (AtmosphereResource)request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        resource.suspend();
        factory.get(channelName).removeAtmosphereResource(resource);
    }
}
