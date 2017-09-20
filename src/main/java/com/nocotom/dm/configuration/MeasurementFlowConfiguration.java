package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceEvent;
import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import com.nocotom.dm.properties.MqttBrokerProperties;
import com.nocotom.dm.utility.Classes;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.influxdb.dto.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.EnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.AbstractMessageHandler;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private static final String MEASUREMENT_INBOUND_CHANNEL_NAME = "MeasurementInboundChannel";

    private static final String MEASUREMENT_PERSISTENCE_CHANNEL_NAME = "MeasurementPersistenceChannel";

    private static final String MEASUREMENT_OUTBOUND_CHANNEL_NAME = "MeasurementOutboundChannel";

    private static final String DEVICE_ID = "DeviceId";

    @Bean
    public IntegrationFlow measurementFlow() {
        return IntegrationFlows.from(MEASUREMENT_INBOUND_CHANNEL_NAME)
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .enrich(MeasurementFlowConfiguration::addDeviceIdHeader)
                .channel(MEASUREMENT_PERSISTENCE_CHANNEL_NAME)
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .gateway(MEASUREMENT_OUTBOUND_CHANNEL_NAME)
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
        adapter.setOutputChannelName(MEASUREMENT_INBOUND_CHANNEL_NAME);
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = MEASUREMENT_OUTBOUND_CHANNEL_NAME)
    public MessageHandler stompMessageHandler(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestinationExpression(
                new FunctionExpression<Message<DeviceMeasurementEvent>>(
                        message -> String.format("/devices/%s/measurement", message.getHeaders().get(DEVICE_ID)))
        );
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }

    @Bean
    @ServiceActivator(inputChannel = MEASUREMENT_PERSISTENCE_CHANNEL_NAME)
    public MessageHandler persistMeasurement(InfluxDBTemplate<Point> influxDBTemplate) {
        return new AbstractMessageHandler() {
            @Override
            protected void handleMessageInternal(Message<?> message) throws Exception {
                DeviceMeasurementEvent measurementEvent =
                        Classes.tryCast(message.getPayload(), DeviceMeasurementEvent.class)
                                .orElseThrow(() -> new MessagingException("The payload is not a DeviceMeasurementEvent instance."));

                List<Point> points = measurementEvent.getPoints()
                                        .stream()
                                        .map(point -> Point
                                                .measurement(measurementEvent.getDeviceId())
                                                .time(point.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                                                .addField("measurement", point.getValue())
                                                .build())
                                        .collect(Collectors.toList());
            }
        };
    }

    private static void addDeviceIdHeader(EnricherSpec enricherSpec){
        enricherSpec.headerFunction(DEVICE_ID, message -> {
            DeviceEvent deviceEvent =
                    Classes.tryCast(message.getPayload(), DeviceEvent.class)
                            .orElseThrow(() -> new MessagingException("The payload is not a DeviceEvent instance."));
            return deviceEvent.getDeviceId();
        });
    }
}
