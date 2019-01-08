package com.eaiggi.api.config.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Profile;


@EnableBinding(EchoStreams.class)
@EnableAutoConfiguration
@Profile({ "Development", "docker", "test" })
public class KafkaEchoStreamConfig {
	
	@Autowired
	private EchoStreams echoStreams;

	public EchoStreams getEchoStreams() {
		return echoStreams;
	}

	public void setEchoStreams(EchoStreams echoStreams) {
		this.echoStreams = echoStreams;
	}	

}
