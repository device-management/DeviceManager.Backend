package com.nocotom.dm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.WebSocketStompSessionManager;
import org.springframework.integration.stomp.outbound.StompMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class StompClientConfiguration {

    @Bean
    public WebSocketClient webSocketClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
        return new SockJsClient(transports);
    }

    @Bean
    public WebSocketStompClient stompClient(){
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(webSocketClient());
        webSocketStompClient.setTaskScheduler(new ConcurrentTaskScheduler());
        return webSocketStompClient;
    }

    @Bean
    public StompSessionManager stompSessionManager(StompBrokerProperties properties) {
        WebSocketStompSessionManager webSocketStompSessionManager =
                new WebSocketStompSessionManager(stompClient(), properties.getUri());
        webSocketStompSessionManager.setAutoReceipt(true);
        webSocketStompSessionManager.setRecoveryInterval(1000);
        return webSocketStompSessionManager;
    }

    @Bean(name = Handlers.BROADCAST_DEVICE_EVENT)
    public MessageHandler broadcastDeviceEvent(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestinationExpression(
                new FunctionExpression<Message<?>>(
                        message -> String.format("/devices/%s", message.getHeaders().get(Headers.DEVICE_ID)))
        );
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }
}
