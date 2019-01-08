package com.eaiggi.api.config.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EchoStreams {
	String INPUT = "kafkaConsumer";
	String OUTPUT = "kafkaPublisher";

	@Input(INPUT)
	SubscribableChannel echoConsumer();
	@Input(OUTPUT)
	MessageChannel echoPublisher();
}
