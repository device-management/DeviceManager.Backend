package com.nocotom.dm.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("mqtt-broker")
public class MqttBrokerProperties {

    public static final String DEFAULT_MQTT_URI = "tcp://localhost:1883";

    public final static String COMMAND_TOPIC_PATTERN = "devices/{0}/command";

    public final static String MEASUREMENT_TOPIC = "devices/+/measurement";

    public final static String REGISTER_TOPIC = "devices/+/register";

    public final static String STATE_TOPIC = "devices/+/state";

    public final static String DEFAULT_USER_NAME = "dm-backend";

    private String uri =  DEFAULT_MQTT_URI;

    private String[] subscribeTopics = new String[] { MEASUREMENT_TOPIC, REGISTER_TOPIC, STATE_TOPIC };

    private String userName = DEFAULT_USER_NAME;
}
