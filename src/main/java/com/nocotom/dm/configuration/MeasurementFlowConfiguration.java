package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import com.nocotom.dm.properties.MqttBrokerProperties;
import org.influxdb.dto.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private static final String MEASUREMENT_INBOUND_CHANNEL_NAME = "MeasurementInboundChannel";

    private static final String MEASUREMENT_PERSISTENCE_CHANNEL_NAME = "MeasurementPersistenceChannel";

    private static final String MEASUREMENT_OUTBOUND_CHANNEL_NAME = "MeasurementOutboundChannel";

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public MeasurementFlowConfiguration(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    @Bean
    public MessageProducer measurementInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                MqttBrokerProperties.DEFAULT_MQTT_URI,
                MqttBrokerProperties.DEFAULT_USER_NAME,
                MqttBrokerProperties.MEASUREMENT_TOPIC
        );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannelName(MEASUREMENT_INBOUND_CHANNEL_NAME);
        return adapter;
    }

    @Bean
    public IntegrationFlow measurementFlow() {
        return IntegrationFlows.from(MEASUREMENT_INBOUND_CHANNEL_NAME)
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .channel(MEASUREMENT_PERSISTENCE_CHANNEL_NAME)
                .gateway(MEASUREMENT_OUTBOUND_CHANNEL_NAME)
                .get();
    }
    
    @ServiceActivator(inputChannel = MEASUREMENT_PERSISTENCE_CHANNEL_NAME)
    public void persistMeasurement(DeviceMeasurementEvent measurementEvent) {
        List<Point> points = measurementEvent.getPoints()
                .stream()
                .map(point -> Point
                        .measurement(measurementEvent.getDeviceId())
                        .time(point.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                        .addField("measurement", point.getValue())
                        .build())
                .collect(Collectors.toList());
        //influxDBTemplate.write(points);
    }

    @MessagingGateway
    @Controller
    public interface MeasurementGateway {

        @MessageMapping("/devices/measurement")
        @Gateway(requestChannel = MEASUREMENT_OUTBOUND_CHANNEL_NAME)
        String gateway(DeviceMeasurementEvent measurementEvent);

    }
}
