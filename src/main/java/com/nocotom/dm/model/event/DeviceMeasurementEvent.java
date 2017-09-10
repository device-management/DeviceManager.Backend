package com.nocotom.dm.model.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.influxdb.dto.Point;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@EqualsAndHashCode
public class DeviceMeasurementEvent {

    @NotNull
    @Size(min=2, max=30)
    @Id
    private String deviceId;

    @NotNull
    private Collection<Point> points;
}
