package com.nocotom.dm.configuration;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class MqttClientConfiguration {

    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttBrokerProperties properties){
        DefaultMqttPahoClientFactory mqttClientFactory = new DefaultMqttPahoClientFactory();
        mqttClientFactory.setServerURIs(properties.getUris());
        return mqttClientFactory;
    }

    @Bean
    public MessageProducer deviceMessageInbound(MqttBrokerProperties properties, MqttPahoClientFactory clientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                properties.getUserName(),
                clientFactory);
        adapter.addTopic(
                MqttBrokerProperties.MEASUREMENT_TOPIC,
                MqttBrokerProperties.REGISTER_TOPIC,
                MqttBrokerProperties.STATE_TOPIC
        );

        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannelName(Channels.DEVICE_CHANNEL);
        return adapter;
    }

    @MessageEndpoint
    public class MqttMessageRouter {

        @Router(inputChannel = Channels.DEVICE_CHANNEL)
        public String route(@Header(MqttHeaders.RECEIVED_TOPIC) String topic){

            if(MqttTopic.isMatched(MqttBrokerProperties.MEASUREMENT_TOPIC, topic)){
                return Channels.MEASUREMENT_CHANNEL;
            }
            if(MqttTopic.isMatched(MqttBrokerProperties.REGISTER_TOPIC, topic)){
                return Channels.REGISTER_CHANNEL;
            }
            if(MqttTopic.isMatched(MqttBrokerProperties.STATE_TOPIC, topic)){
                return Channels.STATE_CHANGED_CHANNEL;
            }
            return null;
        }

    }

}
