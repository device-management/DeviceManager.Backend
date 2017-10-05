package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.stomp.StompSessionManager;
import org.springframework.integration.stomp.outbound.StompMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private static final String MEASUREMENT_FLOW_NAME = "MeasurementFlow";

    private static final String PERSISTENCE_HANDLER_NAME = "MeasurementPersistenceHandler";

    private static final String BROADCAST_HANDLER_NAME = "MeasurementBroadcastHandler";

    @Bean(name = MEASUREMENT_FLOW_NAME)
    public IntegrationFlow measurementFlow(
            @Qualifier(PERSISTENCE_HANDLER_NAME) GenericHandler<DeviceMeasurementEvent> persistenceHandler,
            @Qualifier(BROADCAST_HANDLER_NAME) MessageHandler broadcastHandler) {

        return IntegrationFlows.from(Channels.MEASUREMENT_INBOUND_CHANNEL_NAME)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .enrich(DeviceHeaders::addDeviceIdHeader)
                .handle(persistenceHandler)
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }

    @Bean(name = BROADCAST_HANDLER_NAME)
    public MessageHandler broadcastMeasurement(StompSessionManager stompSessionManager) {
        StompMessageHandler stompMessageHandler = new StompMessageHandler(stompSessionManager);
        stompMessageHandler.setDestinationExpression(
                new FunctionExpression<Message<?>>(
                        message -> String.format("/devices/%s/measurement", message.getHeaders().get(DeviceHeaders.DEVICE_ID)))
        );
        stompMessageHandler.setConnectTimeout(10000);
        return stompMessageHandler;
    }

    @Bean(name = PERSISTENCE_HANDLER_NAME)
    public GenericHandler<DeviceMeasurementEvent> persistMeasurement(InfluxDBTemplate<Point> influxDBTemplate) {
        return (payload, headers) -> {

            List<Point> points = payload.getPoints()
                    .stream()
                    .map(point -> Point
                            .measurement(payload.getDeviceId())
                            .time(point.getTimestamp().toEpochMilli(), TimeUnit.MILLISECONDS)
                            .addField("measurement", point.getValue())
                            .build())
                    .collect(Collectors.toList());

            influxDBTemplate.write(points);

            return payload;
        };
    }


}
