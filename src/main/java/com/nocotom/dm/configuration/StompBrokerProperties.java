package com.nocotom.dm.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.stomp")
public class StompBrokerProperties {

    private static final String DEFAULT_STOMP_URI = "http://localhost:8080/channels";

    private String uri =  DEFAULT_STOMP_URI;

}
