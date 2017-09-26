package com.nocotom.dm.configuration;

import com.nocotom.dm.properties.MqttBrokerProperties;
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
    public MqttPahoClientFactory mqttClientFactory(){
        DefaultMqttPahoClientFactory mqttClientFactory = new DefaultMqttPahoClientFactory();
        mqttClientFactory.setServerURIs(MqttBrokerProperties.DEFAULT_MQTT_URI);
        return mqttClientFactory;
    }

    @Bean
    public MessageProducer deviceMessageInbound(MqttPahoClientFactory clientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                MqttBrokerProperties.DEFAULT_USER_NAME,
                clientFactory);
        adapter.addTopic(
                MqttBrokerProperties.MEASUREMENT_TOPIC,
                MqttBrokerProperties.REGISTER_TOPIC,
                MqttBrokerProperties.STATE_TOPIC
        );

        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannelName(Channels.DEVICE_INBOUND_CHANNEL_NAME);
        return adapter;
    }

    @MessageEndpoint
    public class MqttMessageRouter {

        @Router(inputChannel = Channels.DEVICE_INBOUND_CHANNEL_NAME)
        public String route(@Header(MqttHeaders.RECEIVED_TOPIC) String topic){

            if(MqttTopic.isMatched(MqttBrokerProperties.MEASUREMENT_TOPIC, topic)){
                return Channels.MEASUREMENT_INBOUND_CHANNEL_NAME;
            }
            if(MqttTopic.isMatched(MqttBrokerProperties.REGISTER_TOPIC, topic)){
                return Channels.REGISTER_INBOUND_CHANNEL_NAME;
            }
            if(MqttTopic.isMatched(MqttBrokerProperties.STATE_TOPIC, topic)){
                return Channels.STATE_CHANGED_INBOUND_CHANNEL_NAME;
            }
            return null;
        }

    }

}
