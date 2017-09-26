package com.nocotom.dm.repository;

import com.nocotom.dm.model.Device;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterItem;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class DeviceRepositoryExtensionImpl implements DeviceRepositoryExtension {

    private final ReactiveMongoTemplate mongoTemplate;

    public DeviceRepositoryExtensionImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Device> filter(Filter filter) {

        Criteria criteria = new Criteria();

        if(filter.getFilters().size() > 0){
            Criteria [] filters = filter.getFilters().stream().map(DeviceRepositoryExtensionImpl::parseFilterItem).toArray(Criteria[]::new);
            switch (filter.getLogic()) {
                case ALL:
                    criteria.andOperator(filters);
                    break;
                case ANY:
                    criteria.orOperator(filters);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("The %s operation is not supported.", filter.getLogic()));
            }
        }

        Query query = new Query(criteria);
        if (filter.getLimit() != null) {
            query.limit(filter.getLimit());
        }

        if (filter.getOffset() != null) {
            query.skip(filter.getOffset());
        }

        return mongoTemplate.find(query, Device.class);
    }

    @Override
    public Mono updateState(String deviceId, Map<String, Object> state) {
        Criteria criteria = Criteria.where("id").is(deviceId);
        Query query = new Query(criteria);

        Update update = new Update();
        update.set("state", state);

        return mongoTemplate.update(Device.class).matching(query).apply(update).first();
    }

    private static Criteria parseFilterItem(FilterItem filterItem) {
        Criteria criteria = Criteria.where(filterItem.getKey());
        return filterItem.isExact() ? criteria.is(filterItem.getValue())
                : criteria.regex(filterItem.getValue().toString());
    }
}
