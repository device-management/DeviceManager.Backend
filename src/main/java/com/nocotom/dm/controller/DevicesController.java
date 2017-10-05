package com.nocotom.dm.controller;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterResult;
import com.nocotom.dm.repository.DeviceRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;


@RestController
public class DevicesController implements DevicesApi {

    private final DeviceRepository deviceRepository;

    public DevicesController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Mono<FilterResult> get(@RequestParam(required = false) Integer limit,
                                  @RequestParam(required = false) Integer offset){
        Filter filter = new Filter();
        filter.setLimit(limit);
        filter.setOffset(offset);

        Mono<List<Device>> devices = deviceRepository.filter(filter).collectList();
        Mono<Long> count = deviceRepository.count();
        return devices.and(count).map(results -> new FilterResult(results.getT1(), results.getT2()));
    }

    @Override
    public Mono<Device> get(@PathVariable String deviceId) {
        return deviceRepository.findById(deviceId);
    }

    @Override
    public Mono<FilterResult> find(@Valid @RequestBody Filter filter) {
        Mono<List<Device>> devices = deviceRepository.filter(filter).collectList();
        Mono<Long> count = deviceRepository.count();
        return devices.and(count).map(results -> new FilterResult(results.getT1(), results.getT2()));
    }


}
