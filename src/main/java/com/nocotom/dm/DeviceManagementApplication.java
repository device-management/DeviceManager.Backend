package com.nocotom.dm;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterItem;
import com.nocotom.dm.model.FilteringLogic;
import com.nocotom.dm.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import reactor.core.publisher.Flux;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DeviceManagementApplication implements CommandLineRunner {

	@Autowired
	private DeviceRepository deviceRepository;

	public static void main(String[] args) {
		SpringApplication.run(DeviceManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Device device = new Device();
		device.setDeviceId("testId");
		Map<String, Object> properties = new HashMap<>();
		properties.put("prop1", "val");
		device.setProperties(properties);
		deviceRepository.insert(device).block();
		Filter filter = new Filter();
		List<FilterItem> filters = new LinkedList<>();
		FilterItem filterItem = new FilterItem();
		filterItem.setKey("properties.prop1");
		filterItem.setValue("va");
		filterItem.setExact(false);
		filters.add(filterItem);

		filter.setFilters(filters);
		filter.setLogic(FilteringLogic.ANY);
		Flux<Device> devices = deviceRepository.filter(filter);
		List<Device> collection = devices.collectList().block();
	}

}