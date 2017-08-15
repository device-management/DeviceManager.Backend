package com.nocotom.dm.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.nocotom.dm.model.ChannelEvent;
import lombok.extern.slf4j.Slf4j;
import org.atmosphere.config.managed.Decoder;
import org.atmosphere.config.managed.Encoder;
import org.atmosphere.config.service.*;
import org.atmosphere.cpr.*;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

//@ManagedService(path = "/channel")
@Slf4j
public class ChannelHub {

    private final EventBus eventBus;

    private final BroadcasterFactory factory;

    public ChannelHub(EventBus eventBus, BroadcasterFactory factory) {
        this.eventBus = eventBus;
        this.factory = factory;
    }

    @Ready
    public void onReady(final AtmosphereResource resource) {
        log.debug("Browser {} connected.", resource.uuid());
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        factory.lookupAll().forEach(broadcaster -> broadcaster.removeAtmosphereResource(event.getResource()));
        if (event.isCancelled()) {
            log.debug("Browser {} unexpectedly disconnected.", event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            log.debug("Browser {} closed the connection.", event.getResource().uuid());
        }
    }

    @Path("{channelName}/subscribe")
    @Message
    public void subscribe(AtmosphereResource resource, @PathParam("channelName") String channelName){
        factory.get(channelName).addAtmosphereResource(resource);
    }

    @Path("{channelName}/unsubscribe")
    @Message
    public void unsubscribe(AtmosphereResource resource, @PathParam("channelName") String channelName){
        factory.get(channelName).removeAtmosphereResource(resource);
    }

    @Path("{channelName}")
    @Message(decoders = {JacksonEncoderDecoder.class})
    public void publish(ChannelEvent event, @PathParam("channelName") String channelName){
        eventBus.post(event);
    }

    public static class JacksonEncoderDecoder
            implements Encoder<ChannelEvent, String>, Decoder<String, ChannelEvent> {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String encode(ChannelEvent m) {
            try {
                return this.mapper.writeValueAsString(m);
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public ChannelEvent decode(String s) {
            try {
                return this.mapper.readValue(s, ChannelEvent.class);
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

    }
}
