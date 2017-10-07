package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceStateChangedEvent;
import com.nocotom.dm.model.event.Events;
import com.nocotom.dm.repository.DeviceRepository;
import com.nocotom.dm.utility.DeviceEventToChannelEventTransformer;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Configuration
public class StateChangedFlowConfiguration {

    private static final String STATE_CHANGED_FLOW = "StateChangedFlow";

    private static final String PERSISTENCE_HANDLER = "StateChangedPersistenceHandler";

    @Bean(name = STATE_CHANGED_FLOW)
    public IntegrationFlow registerFlow(
            @Qualifier(Handlers.CONSTRAINTS_VALIDATOR) GenericHandler<Object> constraintValidator,
            @Qualifier(Handlers.BROADCAST_DEVICE_EVENT) MessageHandler broadcastHandler,
            @Qualifier(PERSISTENCE_HANDLER) GenericHandler<DeviceStateChangedEvent> persistenceHandler) {

        return IntegrationFlows.from(Channels.STATE_CHANGED_CHANNEL)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceStateChangedEvent.class))
                .handle(constraintValidator)
                .enrich(Headers::addDeviceIdHeader)
                .handle(persistenceHandler)
                .transform(new DeviceEventToChannelEventTransformer(Events.DEVICE_STATE_CHANGED))
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }

    @Bean(name = PERSISTENCE_HANDLER)
    public GenericHandler<DeviceStateChangedEvent> persistStateChanged(DeviceRepository deviceRepository) {
        return (payload, headers) -> {

            deviceRepository.updateState(payload.getDeviceId(), payload.getState()).block();

            return payload;
        };
    }
}
