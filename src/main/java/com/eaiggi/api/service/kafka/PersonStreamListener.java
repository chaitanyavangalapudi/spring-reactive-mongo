package com.eaiggi.api.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.eaiggi.api.config.kafka.EchoStreams;
import com.eaiggi.api.mongo.model.Person;
import com.eaiggi.api.service.mongo.PersonService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PersonStreamListener {

	@Autowired
	PersonService personRepositoryService;
	
	@StreamListener(EchoStreams.INPUT)
	public void receivePerson(@Payload Person person) {
        log.info("Received Person Object: {}", person);
        personRepositoryService.create(person);
    }
}
