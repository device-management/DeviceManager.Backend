package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceStateChangedEvent;
import com.nocotom.dm.repository.DeviceRepository;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.outbound.StompMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Configuration
public class StateChangedFlowConfiguration {

    private static final String STATE_CHANGED_FLOW_NAME = "StateChangedFlow";

    private static final String PERSISTENCE_HANDLER_NAME = "StateChangedPersistenceHandler";

    private static final String BROADCAST_HANDLER_NAME = "StateChangedBroadcastHandler";

    @Bean(name = STATE_CHANGED_FLOW_NAME)
    public IntegrationFlow registerFlow(
            @Qualifier(Handlers.CONSTRAINTS_VALIDATOR) GenericHandler<Object> constraintValidator,
            @Qualifier(PERSISTENCE_HANDLER_NAME) GenericHandler<DeviceStateChangedEvent> persistenceHandler,
            @Qualifier(BROADCAST_HANDLER_NAME) MessageHandler broadcastHandler) {

        return IntegrationFlows.from(Channels.STATE_CHANGED_INBOUND_CHANNEL_NAME)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceStateChangedEvent.class))
                .handle(constraintValidator)
                .enrich(Headers::addDeviceIdHeader)
                .handle(persistenceHandler)
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }

    @Bean(name = BROADCAST_HANDLER_NAME)
    public MessageHandler broadcastStateChanged(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestinationExpression(
                new FunctionExpression<Message<?>>(
                        message -> String.format("/devices/%s/state", message.getHeaders().get(Headers.DEVICE_ID)))
        );
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }

    @Bean(name = PERSISTENCE_HANDLER_NAME)
    public GenericHandler<DeviceStateChangedEvent> persistStateChanged(DeviceRepository deviceRepository) {
        return (payload, headers) -> {

            deviceRepository.updateState(payload.getDeviceId(), payload.getState()).block();

            return payload;
        };
    }
}
