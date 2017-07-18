package com.nocotom.dm.bootstrap;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.nocotom.dm.configuration.MongoDbConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.nocotom.dm.configuration"} )
public class MongoDbBootstrapper {

    @Bean
    public MongoDatabase bootstrapDatabase(final MongoDbConfiguration configuration){
        String uri = String.format("mongodb://%s:%s", configuration.getHost(), configuration.getPort());
        MongoClient client = new MongoClient(new MongoClientURI(uri));
        return client.getDatabase(configuration.getDatabaseName());
    }

    @Bean
    public MongoCollection bootstrapCollection(final MongoDbConfiguration configuration,
                                               final MongoDatabase database){
        return database.getCollection(configuration.getCollectionName());
    }
}
