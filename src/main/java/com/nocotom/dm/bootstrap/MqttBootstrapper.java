package com.nocotom.dm.bootstrap;

import com.nocotom.dm.configuration.MqttBrokerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttBootstrapper {
    private final MqttBrokerProperties brokerProperties;

    public MqttBootstrapper(MqttBrokerProperties brokerProperties) {
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
