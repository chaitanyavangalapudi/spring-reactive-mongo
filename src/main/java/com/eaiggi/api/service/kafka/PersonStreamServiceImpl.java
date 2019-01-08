package com.eaiggi.api.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.eaiggi.api.config.kafka.EchoStreams;
import com.eaiggi.api.mongo.model.Person;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PersonStreamServiceImpl implements PersonStreamService {
	
	private final EchoStreams echoStreams;

	@Autowired
	public PersonStreamServiceImpl(EchoStreams echoStreams) {		
		this.echoStreams = echoStreams;
	}

	@Override
	public void send(final Person person) {
		log.info("Sending Person {}", person);
		 
        MessageChannel messageChannel = echoStreams.echoPublisher();
        messageChannel.send(MessageBuilder
                .withPayload(person)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());		
	}

}
