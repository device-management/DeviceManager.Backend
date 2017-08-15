package com.nocotom.dm.handler;

import com.nocotom.dm.model.ChannelEvent;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

//@Path("/")
public class ExampleJersey {
/*
    @Context
    private HttpServletRequest request;

    @Path("{channelName}/subscribe")
    @POST
    public void subscribe(@PathParam("channelName") String channelName){
        AtmosphereResource resource = (AtmosphereResource) request.getAttribute(ApplicationConfig.ATMOSPHERE_RESOURCE);
        //factory.get(channelName).addAtmosphereResource(resource);
    }

    @Path("{channelName}/unsubscribe")
    @POST
    public void unsubscribe(@PathParam("channelName") String channelName){
        int asd = 4;
        //factory.get(channelName).removeAtmosphereResource(resource);
    }

    @Path("{channelName}")
    @POST
    public void publish(@PathParam("channelName") String channelName){
        int x = 5;
        //eventBus.post(event);
    }*/
}
