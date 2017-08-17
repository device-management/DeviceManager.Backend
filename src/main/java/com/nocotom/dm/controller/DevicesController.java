package com.nocotom.dm.controller;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
public class DevicesController implements DevicesApi {

    @Override
    public Mono<FilterResult> get(@RequestParam(required = false) Integer limit,
                                  @RequestParam(required = false) Integer offset){
        return Mono.empty();
    }

    @Override
    public Mono<Device> get(@PathVariable String deviceId) {
        return Mono.empty();
    }

    @Override
    public Mono<FilterResult> find(@Valid @RequestBody Filter filter) {
        return Mono.empty();
    }


}
