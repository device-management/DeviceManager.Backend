package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceEvent;
import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import com.nocotom.dm.properties.MqttBrokerProperties;
import com.nocotom.dm.utility.Classes;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.EnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.outbound.StompMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private static final String INBOUND_CHANNEL_NAME = "MeasurementInboundChannel";

    private static final String PERSISTENCE_HANDLER_NAME = "MeasurementPersistenceHandler";

    private static final String BROADCAST_HANDLER_NAME = "MeasurementBroadcastHandler";

    private static final String DEVICE_ID = "DeviceId";

    @Bean
    public IntegrationFlow measurementFlow(
            @Qualifier(PERSISTENCE_HANDLER_NAME) GenericHandler<Object> persistenceHandler,
            @Qualifier(BROADCAST_HANDLER_NAME) MessageHandler broadcastHandler) {

        return IntegrationFlows.from(INBOUND_CHANNEL_NAME)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .enrich(MeasurementFlowConfiguration::addDeviceIdHeader)
                .handle(persistenceHandler)
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }

    @Bean
    public MessageProducer measurementInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                MqttBrokerProperties.DEFAULT_MQTT_URI,
                MqttBrokerProperties.DEFAULT_USER_NAME,
                MqttBrokerProperties.MEASUREMENT_TOPIC
        );
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannelName(INBOUND_CHANNEL_NAME);
        return adapter;
    }

    @Bean(name = BROADCAST_HANDLER_NAME)
    public MessageHandler broadcastMeasurement(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestinationExpression(
                new FunctionExpression<Message<?>>(
                        message -> String.format("/devices/%s/measurement", message.getHeaders().get(DEVICE_ID)))
        );
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }

    @Bean(name = PERSISTENCE_HANDLER_NAME)
    public GenericHandler<Object> persistMeasurement(InfluxDBTemplate<Point> influxDBTemplate) {
        return (payload, headers) -> {
            DeviceMeasurementEvent measurementEvent =
                    Classes.tryCast(payload, DeviceMeasurementEvent.class)
                            .orElseThrow(() -> new MessagingException("The payload is not a DeviceMeasurementEvent instance."));

            List<Point> points = measurementEvent.getPoints()
                    .stream()
                    .map(point -> Point
                            .measurement(measurementEvent.getDeviceId())
                            .time(point.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                            .addField("measurement", point.getValue())
                            .build())
                    .collect(Collectors.toList());

            return payload;
        };
    }

    private static void addDeviceIdHeader(EnricherSpec enricherSpec) {
        enricherSpec.headerFunction(DEVICE_ID, message -> {
            DeviceEvent deviceEvent =
                    Classes.tryCast(message.getPayload(), DeviceEvent.class)
                            .orElseThrow(() -> new MessagingException("The payload is not a DeviceEvent instance."));
            return deviceEvent.getDeviceId();
        });
    }
}
