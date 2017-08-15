package com.nocotom.dm.controller;

import com.nocotom.dm.MongoDb;
import com.nocotom.dm.MqttBroker;
import com.nocotom.dm.model.Filter;
import com.nocotom.dm.model.FilterResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DevicesControllerITest {

    private static final String BASE_URL = "/api/devices";

    private final MongoDb mongoDb = new MongoDb();

    private final MqttBroker mqttBroker = new MqttBroker();

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception{
        mongoDb.run();
        mqttBroker.run();
    }

    @After
    public void cleanUp() throws Exception{
        mongoDb.tearDown();
        mqttBroker.tearDown();
    }

    @Test
    public void shouldValidateFilterOffsetParameter(){
        // GIVEN
        Filter filter = new Filter();
        filter.setOffset(-1);

        // WHEN
        ResponseEntity<FilterResult> filterResult = restTemplate.postForEntity(BASE_URL + "/find", filter, FilterResult.class);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, filterResult.getStatusCode());
    }

    @Test
    public void shouldValidateFilterLimitParameter() {
        // GIVEN
        Filter filter = new Filter();
        filter.setLimit(-100);

        // WHEN
        ResponseEntity<FilterResult> filterResult = restTemplate.postForEntity(BASE_URL + "/find", filter, FilterResult.class);

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, filterResult.getStatusCode());
    }


}
