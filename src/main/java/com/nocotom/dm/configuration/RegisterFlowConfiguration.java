package com.nocotom.dm.configuration;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.event.DeviceEvent;
import com.nocotom.dm.model.event.DeviceRegisterEvent;
import com.nocotom.dm.model.event.Events;
import com.nocotom.dm.repository.DeviceRepository;
import com.nocotom.dm.utility.DeviceEventToChannelEventTransformer;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
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
public class RegisterFlowConfiguration {

    private static final String REGISTER_FLOW = "RegisterFlow";

    private static final String NEW_DEVICE_REGISTER_FLOW = "NewDeviceRegisterFlow";

    private static final String EXISTING_DEVICE_REGISTER_FLOW = "DefaultRegisterFlow";

    private static final String DEVICE_EXISTENCE_ROUTER = "DeviceExistenceRouter";

    private static final String REGISTER_NEW_DEVICE_CHANNEL = "RegisterNewDeviceChannel";

    private static final String REGISTER_EXISTING_DEVICE_CHANNEL = "RegisterExistingDeviceChannel";

    private static final String BROADCAST_NEW_DEVICE_EVENT = "BroadcastNewDeviceEvent";

    private static final String PERSISTENCE_HANDLER = "RegisterPersistenceHandler";

    @Bean(name = REGISTER_FLOW)
    public IntegrationFlow registerFlow(
            @Qualifier(Handlers.CONSTRAINTS_VALIDATOR) GenericHandler<Object> constraintValidator
            ) {

        return IntegrationFlows
                .from(Channels.REGISTER_CHANNEL)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceRegisterEvent.class))
                .handle(constraintValidator)
                .enrich(Headers::addDeviceIdHeader)
                .channel(DEVICE_EXISTENCE_ROUTER)
                .get();
    }

    @Bean(name = NEW_DEVICE_REGISTER_FLOW)
    public IntegrationFlow newDeviceRegisterFlow(
            @Qualifier(PERSISTENCE_HANDLER) GenericHandler<DeviceRegisterEvent> persistenceHandler,
            @Qualifier(BROADCAST_NEW_DEVICE_EVENT) MessageHandler broadcastNewDeviceHandler
    ){
        return IntegrationFlows
                .from(REGISTER_NEW_DEVICE_CHANNEL)
                .handle(persistenceHandler)
                .transform(new DeviceEventToChannelEventTransformer(Events.DEVICE_REGISTERED))
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastNewDeviceHandler)
                .get();
    }

    @Bean(name = EXISTING_DEVICE_REGISTER_FLOW)
    public IntegrationFlow existingDeviceRegisterFlow(
            @Qualifier(PERSISTENCE_HANDLER) GenericHandler<DeviceRegisterEvent> persistenceHandler,
            @Qualifier(Handlers.BROADCAST_DEVICE_EVENT) MessageHandler broadcastHandler
    ){
        return IntegrationFlows
                .from(REGISTER_EXISTING_DEVICE_CHANNEL)
                .handle(persistenceHandler)
                .transform(new DeviceEventToChannelEventTransformer(Events.DEVICE_REGISTERED))
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }


    @Bean(name = PERSISTENCE_HANDLER)
    public GenericHandler<DeviceRegisterEvent> persistDevice(DeviceRepository deviceRepository) {
        return (payload, headers) -> {

            Device device = new Device(
                    payload.getDeviceId(),
                    payload.getName(),
                    payload.getType(),
                    payload.getConfiguration(),
                    payload.getState()
            );

            deviceRepository.save(device).block();
            return payload;
        };
    }

    @Bean(name = BROADCAST_NEW_DEVICE_EVENT)
    public MessageHandler broadcastNewDeviceEvent(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestination("/devices");
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }

    @MessageEndpoint
    public class RegisterMessageRouter {

        private final DeviceRepository deviceRepository;

        public RegisterMessageRouter(DeviceRepository deviceRepository) {
            this.deviceRepository = deviceRepository;
        }

        @Router(inputChannel = DEVICE_EXISTENCE_ROUTER)
        public String route(Message<DeviceEvent> message){

            Boolean exist = deviceRepository.existsById(message.getPayload().getDeviceId()).block();
            if(exist == null){
                return null;
            }

            return exist ? REGISTER_EXISTING_DEVICE_CHANNEL : REGISTER_NEW_DEVICE_CHANNEL;
        }

    }

}
