package com.nocotom.dm.model.event;

import com.nocotom.dm.model.Point;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@EqualsAndHashCode
public class DeviceMeasurementEvent {

    @NotNull
    @Size(min=2, max=30)
    private String deviceId;

    @NotNull
    private Collection<Point> points;
}
