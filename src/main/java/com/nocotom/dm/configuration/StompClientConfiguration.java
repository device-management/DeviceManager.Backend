package com.nocotom.dm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.WebSocketStompSessionManager;
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
    public StompSessionManager stompSessionManager() {
        WebSocketStompSessionManager webSocketStompSessionManager =
                new WebSocketStompSessionManager(stompClient(), "http://localhost:8080/channels");
        webSocketStompSessionManager.setAutoReceipt(true);
        webSocketStompSessionManager.setRecoveryInterval(1000);
        return webSocketStompSessionManager;
    }
}
