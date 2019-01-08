package com.eaiggi.api.config.reactive;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.eaiggi.api.config.mongo.MongoSettings;
import com.eaiggi.api.exception.BaseException;
import com.eaiggi.api.mongo.MongoConnectReactiveUtils;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.WriteConcern;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SslSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableReactiveMongoRepositories
@Profile({ "reactive" })
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration {

	@Autowired
	MongoSettings mongoSettings;

	private NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

	@Override
	public MongoClient reactiveMongoClient() {
		MongoClient mongoClient = null;
		try {
			MongoClientSettings mongoClientSettings = createMongoClientSettings(mongoSettings);
			mongoClient = MongoClients.create(mongoClientSettings);;
		} catch (BaseException e) {
			log.error("Error creating Reactive Mongo Client", e);
		}
		return mongoClient;
	}

	@Override
	protected String getDatabaseName() {
		return mongoSettings.getDatabaseName();
	}

	@Bean
	public MongoClientSettings createMongoClientSettings(MongoSettings mongoConnectionParameters)
			throws BaseException {
		MongoClientSettings mongoClientSettings = null;
		log.info("Initializing Reactive Mongo Client Settings with Parameters:{}", mongoConnectionParameters);
		try {
			SslSettings sslSettings = MongoConnectReactiveUtils.createSslSettings(mongoConnectionParameters.getSslConfig());
			ConnectionPoolSettings connPoolSettings = ConnectionPoolSettings.builder().minSize(1).build();
			ClusterSettings clusterSettings = MongoConnectReactiveUtils
					.createClusterSettings(mongoConnectionParameters.getCluster());
			MongoCredential credential = MongoConnectReactiveUtils.getMongoCredential(mongoConnectionParameters);

			log.debug("Initializing Reactive Mongo client Settings  ....");
			mongoClientSettings = MongoClientSettings.builder().credential(credential).applyToClusterSettings(unit -> unit.applySettings(clusterSettings))
			        .applyToSslSettings(unit-> unit.applySettings(sslSettings))
					.streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
					.writeConcern(WriteConcern.ACKNOWLEDGED).build();
		} catch (Exception e) {
			log.error("Error while creating Reactive Mongo Client Settings", e);
			throw new BaseException("Error while creating Reactive Mongo Client Settings", e);
		}
		return mongoClientSettings;
	}

	@PreDestroy
	public void shutDownEventLoopGroup() {
		eventLoopGroup.shutdownGracefully();
	}
}
