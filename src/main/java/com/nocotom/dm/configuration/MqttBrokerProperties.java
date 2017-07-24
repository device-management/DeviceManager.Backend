package com.nocotom.dm.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("mqtt-broker")
public class MqttBrokerProperties {

    private static final int DEFAULT_MQTT_BROKER_PORT = 1883;

    private static final String DEFAULT_MQTT_BROKER_HOST = "localhost";

    private String host = DEFAULT_MQTT_BROKER_HOST;

    private int port = DEFAULT_MQTT_BROKER_PORT;
}
