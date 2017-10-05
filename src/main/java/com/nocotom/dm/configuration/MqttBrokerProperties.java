package com.nocotom.dm.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.mqtt")
public class MqttBrokerProperties {

    public final static String COMMAND_TOPIC_PATTERN = "devices/{0}/command";

    public final static String MEASUREMENT_TOPIC = "devices/+/measurement";

    public final static String REGISTER_TOPIC = "devices/+/register";

    public final static String STATE_TOPIC = "devices/+/state";

    private static final String DEFAULT_MQTT_URI = "tcp://localhost:1883";

    private final static String DEFAULT_USER_NAME = "dm-backend";

    private String[] uris =  { DEFAULT_MQTT_URI };

    private String userName = DEFAULT_USER_NAME;
}
