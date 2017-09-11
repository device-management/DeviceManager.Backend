package com.nocotom.dm.configuration;

import com.nocotom.dm.properties.MqttBrokerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
public class MqttConfiguration {
    private final MqttBrokerProperties brokerProperties;

    public MqttConfiguration(MqttBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }


    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(brokerProperties.getUri());
        factory.setUserName(brokerProperties.getUserName());
        return factory;
    }
/*
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(brokerProperties.getUserName(), mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("devices/devId/register");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {

        void sendToMqtt(String data);

    }*/
}
