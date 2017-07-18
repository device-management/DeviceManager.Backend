package com.nocotom.dm.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mongo-db")
public class MongoDbConfiguration {

    private static final int DEFAULT_MONGO_DB_PORT = 27017;

    private static final String DEFAULT_MONGO_DB_HOST = "localhost";

    private static final String DEFAULT_DATABASE_NAME = "deviceManager";

    private static final String DEFAULT_COLLECTION_NAME = "devices";

    private String host = DEFAULT_MONGO_DB_HOST;

    private int port = DEFAULT_MONGO_DB_PORT;

    private String databaseName = DEFAULT_DATABASE_NAME;

    private String collectionName = DEFAULT_COLLECTION_NAME;
}
