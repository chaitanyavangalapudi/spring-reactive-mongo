package com.eaiggi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication (exclude={ MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, 
		                  MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class })
public class IggiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IggiApplication.class, args);
	}	
}
