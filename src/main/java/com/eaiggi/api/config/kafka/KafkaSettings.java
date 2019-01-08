package com.eaiggi.api.config.kafka;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
@ToString(exclude = "password")
public class KafkaSettings {

	private boolean clientAuthRequired;
	private boolean enable2eEncryption;
	private Integer lingerMs;

	@NotNull
	private String username;
	@NotNull
	private String password;

	@NotNull
	private String topicName;

	private String cryptoRsaPrivateKeyPath;
	private String cryptoRsaPublicKeyPath;

	private String newTopic;

	private String messageKey;
}
