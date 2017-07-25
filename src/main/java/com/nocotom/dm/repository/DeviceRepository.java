package com.nocotom.dm.repository;

import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterItem;
import com.nocotom.dm.model.FilterResult;
import org.bson.RawBsonDocument;
import org.bson.conversions.Bson;
import org.springframework.core.serializer.Deserializer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DeviceRepository {

    private final MongoCollection<RawBsonDocument> mongoCollection;

    private final Deserializer<Device> deviceDeserializer;

    public DeviceRepository(MongoCollection<RawBsonDocument> mongoCollection, Deserializer<Device> deviceDeserializer) {
        this.mongoCollection = mongoCollection;
        this.deviceDeserializer = deviceDeserializer;
    }

    public CompletableFuture<FilterResult> filter(Filter filter) {
        Bson filterBson;
        switch (filter.getLogic()) {
            case ALL:
                filterBson = Filters.and(filter.getFilters().stream().map(DeviceRepository::parseFilter).toArray(Bson[]::new));
                break;
            case ANY:
                filterBson = Filters.or(filter.getFilters().stream().map(DeviceRepository::parseFilter).toArray(Bson[]::new));
                break;
            default:
                throw new UnsupportedOperationException(String.format("The %s operation is not supported.", filter.getLogic()));
        }

        FindIterable<RawBsonDocument> findIterable = mongoCollection.find(filterBson);
        if (filter.getLimit() != null) {
            findIterable.limit(filter.getLimit());
        }

        if (filter.getOffset() != null) {
            findIterable.skip(filter.getOffset());
        }

        final CompletableFuture<Long> completableCount = new CompletableFuture<>();
        mongoCollection.count((devicesAmount, throwable) -> {
            if (throwable == null) completableCount.complete(devicesAmount);
            else completableCount.completeExceptionally(throwable);
        });

        List<Device> devices = new LinkedList<>();
        final CompletableFuture<List<Device>> completableFind = new CompletableFuture<>();
        findIterable.forEach(document -> {
            try {
                Device device = deviceDeserializer.deserialize(new ByteArrayInputStream(document.getByteBuffer().array()));
                devices.add(device);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }, (result, throwable) -> {
            if (throwable == null) completableFind.complete(devices);
            else completableFind.completeExceptionally(throwable);
        });

        return CompletableFuture.<FilterResult>allOf(completableCount, completableFind)
                .thenApplyAsync(aVoid -> new FilterResult(completableFind.join(), completableCount.join()));
    }

    public CompletableFuture insertOrUpdate(Device device) {
        return null;
    }


    private static Bson parseFilter(FilterItem filterItem) {
        return filterItem.isExact() ? Filters.eq(filterItem.getKey(), filterItem.getValue())
                : Filters.regex(filterItem.getKey(), filterItem.getValue());
    }
}
