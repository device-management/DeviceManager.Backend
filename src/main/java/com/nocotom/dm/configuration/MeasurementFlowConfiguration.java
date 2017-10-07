package com.nocotom.dm.configuration;

import com.nocotom.dm.model.event.DeviceMeasurementEvent;
import com.nocotom.dm.model.event.Events;
import com.nocotom.dm.utility.DeviceEventToChannelEventTransformer;
import com.nocotom.dm.utility.StringToByteArrayTransformer;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class MeasurementFlowConfiguration {

    private static final String MEASUREMENT_FLOW = "MeasurementFlow";

    private static final String PERSISTENCE_HANDLER = "MeasurementPersistenceHandler";

    @Bean(name = MEASUREMENT_FLOW)
    public IntegrationFlow measurementFlow(
            @Qualifier(Handlers.CONSTRAINTS_VALIDATOR) GenericHandler<Object> constraintValidator,
            @Qualifier(Handlers.BROADCAST_DEVICE_EVENT) MessageHandler broadcastHandler,
            @Qualifier(PERSISTENCE_HANDLER) GenericHandler<DeviceMeasurementEvent> persistenceHandler
            ) {

        return IntegrationFlows.from(Channels.MEASUREMENT_CHANNEL)
                .channel(c -> c.executor(Executors.newCachedThreadPool()))
                .transform(new JsonToObjectTransformer(DeviceMeasurementEvent.class))
                .handle(constraintValidator)
                .enrich(Headers::addDeviceIdHeader)
                .handle(persistenceHandler)
                .transform(new DeviceEventToChannelEventTransformer(Events.MEASUREMENT_OCCURRED))
                .transform(new ObjectToJsonTransformer())
                .transform(new StringToByteArrayTransformer(StandardCharsets.UTF_8))
                .handle(broadcastHandler)
                .get();
    }

    @Bean(name = PERSISTENCE_HANDLER)
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
