package com.nocotom.dm.controller;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RequestMapping(path = "api/devices", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public interface DevicesApi {

    @GetMapping(consumes = MediaType.ALL_VALUE)
    Mono<FilterResult> get(@RequestParam(required = false) Integer limit,
                           @RequestParam(required = false) Integer offset);

    @GetMapping(value = "/{deviceId}", consumes = MediaType.ALL_VALUE)
    Mono<Device> get(@PathVariable String deviceId);

    @PostMapping(value = "/find", consumes = MediaType.ALL_VALUE)
    Mono<FilterResult> find(@Valid @RequestBody Filter filter);

    @PostMapping(value = "/{deviceId}/command", consumes = MediaType.ALL_VALUE)
    void command(@PathVariable String deviceId,
                 @RequestBody Map<String, Object> properties);
}
