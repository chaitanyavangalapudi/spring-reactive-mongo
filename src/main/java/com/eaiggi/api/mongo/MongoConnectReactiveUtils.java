package com.eaiggi.api.mongo;

import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import com.eaiggi.api.config.mongo.MongoSettings;
import com.eaiggi.api.config.mongo.MongoSettings.Cluster;
import com.eaiggi.api.exception.BaseException;
import com.eaiggi.api.security.SSLParameters;
import com.eaiggi.api.security.SSLUtils;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ClusterType;
import com.mongodb.connection.SslSettings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoConnectReactiveUtils {

	public static SslSettings createSslSettings(SSLParameters sslParameters) throws BaseException {
		if (sslParameters == null) {
			throw new BaseException("Mongo SSL Parameters cannot be NULL");
		}
		SslSettings sslSettings = null;
		try {
			SSLContext sslContext = SSLUtils.createSslContext(sslParameters);
		} catch (Exception e) {
			throw new BaseException("Error while creating Reactive Mongo SSL Settings", e);
		}
		return sslSettings;
	}

	private static ClusterType getClusterType(String clusterType) {
		for (ClusterType type : ClusterType.values()) {
			if (type.name().equals(clusterType)) {
				return type;
			}
		}
		return ClusterType.STANDALONE;
	}

	private static ClusterConnectionMode getClusterConnectionMode(String clusterConnectionMode) {
		for (ClusterConnectionMode type : ClusterConnectionMode.values()) {
			if (type.name().equals(clusterConnectionMode)) {
				return type;
			}
		}
		return ClusterConnectionMode.SINGLE;
	}

	public static ClusterSettings createClusterSettings(Cluster clusterConfig) throws BaseException {
		if (clusterConfig == null || clusterConfig.getHosts() == null) {
			throw new BaseException("Mongo Server Port Map cannot be NULL");
		}
		log.info("Initializing Reactive Mongo Client Settings with cluster Parameters:{}", clusterConfig);
		ClusterSettings clusterSettings = null;
		try {
			ClusterType clusterType = getClusterType(clusterConfig.getType().toUpperCase());
			ClusterConnectionMode clusterConnectionMode = getClusterConnectionMode(
					clusterConfig.getConnectionMode().toUpperCase());
			List<ServerAddress> hosts = clusterConfig.getHosts().stream()
					.map(hostEntry -> new ServerAddress(hostEntry.getHost(), hostEntry.getPort()))
					.collect(Collectors.toList());
			clusterSettings = ClusterSettings.builder().mode(clusterConnectionMode).requiredClusterType(clusterType)
					.hosts(hosts).build();
		} catch (Exception e) {
			throw new BaseException("Error while creating Reactive Mongo Cluster Settings", e);
		}
		return clusterSettings;

	}

	public static MongoCredential getMongoCredential(MongoSettings mongoConnectionParameters) {
		log.debug("Initializing Reactive Mongo credentials");
		return MongoCredential.createCredential(mongoConnectionParameters.getUsername(),
				mongoConnectionParameters.getDatabaseName(), mongoConnectionParameters.getPassword().toCharArray());
	}
}
