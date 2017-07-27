package com.nocotom.dm.repository;

import com.nocotom.dm.model.Device;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends FilterableDeviceRepository, ReactiveMongoRepository<Device, String> {
}
