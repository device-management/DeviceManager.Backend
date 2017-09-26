package com.nocotom.dm.repository;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface DeviceRepositoryExtension {

    Flux<Device> filter(Filter filter);

    Mono updateState(String deviceId, Map<String, Object> state);
}
