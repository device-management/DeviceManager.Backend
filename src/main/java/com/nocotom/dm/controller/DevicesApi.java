package com.nocotom.dm.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = "api/devices", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public interface DevicesApi {


}
