package com.nocotom.dm.repository;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import reactor.core.publisher.Flux;

public interface FilterableDeviceRepository {

    Flux<Device> filter(Filter filter);
}
