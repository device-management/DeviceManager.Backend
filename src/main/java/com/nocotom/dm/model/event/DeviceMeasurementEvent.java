package com.nocotom.dm.model.event;

import com.nocotom.dm.model.Point;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceMeasurementEvent extends DeviceEvent {

    @NotNull
    private Collection<Point> points;
}
