package com.nocotom.dm.bootstrap;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.nocotom.dm.configuration.MongoDbProperties;
import org.bson.RawBsonDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Configuration
@ComponentScan(basePackages = {"com.nocotom.dm.configuration"})
public class MongoDbBootstrapper {

    @Bean
    @Scope(SCOPE_SINGLETON)
    public MongoDatabase bootstrapDatabase(final MongoDbProperties configuration) {
        String uri = String.format("mongodb://%s:%s", configuration.getHost(), configuration.getPort());
        MongoClient mongoClient = MongoClients.create(uri);
        return mongoClient.getDatabase(configuration.getDatabaseName());
    }

    @Bean
    public MongoCollection<RawBsonDocument> bootstrapCollection(final MongoDbProperties configuration,
                                                                final MongoDatabase database) {
        return database.getCollection(configuration.getCollectionName(), RawBsonDocument.class);
    }
}
