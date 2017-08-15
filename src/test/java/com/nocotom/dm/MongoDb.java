package com.nocotom.dm;

import com.mongodb.Mongo;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public class MongoDb {

    private static final MongodStarter STARTER = MongodStarter.getDefaultInstance();

    private static final String HOST = "localhost";

    private static final int PORT = 12345;

    private static final String DATABASE_NAME = "TestDb";

    private MongodExecutable mongoExecutable;

    private MongodProcess mongoProcess;

    private ReactiveMongoTemplate mongo;

    public void run() throws Exception {

        mongoExecutable = STARTER.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net("localhost", 12345, Network.localhostIsIPv6()))
                .build());
        mongoProcess = mongoExecutable.start();

        MongoClient mongoClient = MongoClients.create("mongodb://" + HOST + ":" + PORT);
        mongo = new ReactiveMongoTemplate(mongoClient, DATABASE_NAME);
    }

    public void tearDown() throws Exception {
        mongoProcess.stop();
        mongoExecutable.stop();
    }

    public ReactiveMongoTemplate getClient() {
        return mongo;
    }
}
