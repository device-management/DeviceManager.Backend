package com.nocotom.dm.configuration;

import com.nocotom.dm.properties.MqttBrokerProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttInboundConfiguration {

    private final MqttBrokerProperties brokerProperties;

    public MqttInboundConfiguration(MqttBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }
/*
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(brokerProperties.getUri(), brokerProperties.getUserName(),
                        brokerProperties.getSubscribeTopics());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(ConfigurableApplicationContext context) {
        return message -> {
            MqttBootstrapper.MyGateway gateway = context.getBean(MqttBootstrapper.MyGateway.class);
            System.out.println(message.getPayload());
            gateway.sendToMqtt("Hello client!");
        };
    }

*/
}
