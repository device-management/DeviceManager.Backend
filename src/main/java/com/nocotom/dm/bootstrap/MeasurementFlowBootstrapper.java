package com.nocotom.dm.bootstrap;

import com.nocotom.dm.configuration.MqttBrokerProperties;
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

@Configuration
public class MeasurementFlowBootstrapper {

    private final MqttPahoClientFactory mqttClientFactory;

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public MeasurementFlowBootstrapper(MqttPahoClientFactory mqttClientFactory, InfluxDBTemplate<Point> influxDBTemplate) {
        this.mqttClientFactory = mqttClientFactory;
        this.influxDBTemplate = influxDBTemplate;
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
    public IntegrationFlow measurementFlow() {
        return IntegrationFlows.from(measurementInbound())
                .transform(new JsonToObjectTransformer())
                .handle(message -> {
                    int a = 4;
                })
                .get();
    }
}
