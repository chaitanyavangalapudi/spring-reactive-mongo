package com.eaiggi.api.config.mongo;

import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.eaiggi.api.config.mongo.MongoSettings.Cluster;
import com.eaiggi.api.exception.BaseException;
import com.eaiggi.api.security.SSLUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MongoConfig extends AbstractMongoConfiguration {

	@Autowired
	MongoSettings mongoSettings;

	@Bean
	public MongoClientOptions mongoClientOptions(MongoSettings mongoSettings) throws BaseException {
		MongoClientOptions options = null;
		try {
			MongoClientOptions.Builder builder = MongoClientOptions.builder();
			SSLContext sslContext = SSLUtils.createSslContext(mongoSettings.getSslConfig());						
			options = builder.sslEnabled(true).sslContext(sslContext).readConcern(ReadConcern.MAJORITY).heartbeatConnectTimeout(mongoSettings.getDbConnectionTimeout()).
			                sslInvalidHostNameAllowed(false).writeConcern(WriteConcern.ACKNOWLEDGED).build();
		} catch (Exception e) {
			log.error("Error while creating Mongo Client Options", e);
			throw new BaseException("Error while creating Mongo Client Options", e);
		}
		return options;
	}	

	@Override
	public MongoClient mongoClient() {
		MongoClient mongoClient = null;
		try {
			MongoClientOptions mongoClientOptions = mongoClientOptions(mongoSettings);
			MongoCredential credential = getMongoCredential();
			List<ServerAddress> serverList = getServerList(mongoSettings.getCluster());
			mongoClient = new MongoClient(serverList, credential, mongoClientOptions);
		} catch (BaseException e) {
			log.error("Error creating Mongo Client", e);			
		}
		return mongoClient;
	}

	@Override
	protected String getDatabaseName() {
		return mongoSettings.getDatabaseName();
	}
	@Bean
	public MongoCredential getMongoCredential() {
		log.debug("Initializing Mongo credentials");
		return MongoCredential.createCredential(mongoSettings.getUsername(),
				mongoSettings.getDatabaseName(), mongoSettings.getPassword().toCharArray());
	}	
	
	public List<ServerAddress> getServerList(Cluster clusterConfig) {
		List<ServerAddress> hosts = clusterConfig.getHosts().stream()
				.map(hostEntry -> new ServerAddress(hostEntry.getHost(), hostEntry.getPort()))
				.collect(Collectors.toList());
		return hosts;
	}
	
}
