package com.nocotom.dm.configuration;

import com.nocotom.dm.properties.MqttBrokerProperties;

import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import org.influxdb.dto.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.outbound.StompMessageHandler;
import org.springframework.messaging.MessageHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private final MqttPahoClientFactory mqttClientFactory;

    private final InfluxDBTemplate<Point> influxDBTemplate;

    private final StompSessionManager sessionManager;

    public MeasurementFlowConfiguration(MqttPahoClientFactory mqttClientFactory, InfluxDBTemplate<Point> influxDBTemplate, StompSessionManager sessionManager) {
        this.mqttClientFactory = mqttClientFactory;
        this.influxDBTemplate = influxDBTemplate;
        this.sessionManager = sessionManager;
    }

    @Bean
    public MessageProducerSupport measurementInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("measurementConsumer",
                mqttClientFactory, MqttBrokerProperties.MEASUREMENT_TOPIC);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(converter);
        adapter.setQos(1);
        return adapter;
    }

    @Bean
    public MessageHandler measurementOutbound() {
        StompMessageHandler messageHandler = new StompMessageHandler(sessionManager);
        messageHandler.setDestination("topic");
        return messageHandler;
    }

    @Bean
    public IntegrationFlow measurementFlow() {
        return IntegrationFlows.from(measurementInbound())
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .handle(message -> {
                    DeviceMeasurementEvent event = ((DeviceMeasurementEvent) message.getPayload());
                    List<Point> points = event.getPoints()
                            .stream()
                            .map(point -> Point
                                    .measurement(event.getDeviceId())
                                    .time(point.getTimestamp().getEpochSecond(), TimeUnit.SECONDS)
                                    .addField("measurement", point.getValue())
                                    .build())
                            .collect(Collectors.toList());
                    influxDBTemplate.write(points);
                }).handle(measurementOutbound())
                .get();
    }
}
