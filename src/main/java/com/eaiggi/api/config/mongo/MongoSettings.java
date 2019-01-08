package com.eaiggi.api.config.mongo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import com.eaiggi.api.security.SSLParameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ConfigurationProperties(prefix = "mongodb")
@Getter
@Setter
@ToString(exclude="password")
public class MongoSettings {
	private boolean enableTls;
	private boolean tlsValidate;
	private boolean allowConnectionsWithoutCertificates;
	private Integer dbConnectionTimeout;

	@NotNull
	private String uri;
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String databaseName;	
	
	@NestedConfigurationProperty
	private SSLParameters sslConfig;
	
	private Cluster cluster;
	
	@Getter
	@Setter
	@ToString
	public static class Cluster {
		private String type;
		private String connectionMode;
		private List<Host> hosts= new ArrayList<>();
		@Getter
		@Setter
		@ToString
		public static class Host {
			@NotNull
			private String host;
			@NotNull
			private Integer port;
		}
	}
}
