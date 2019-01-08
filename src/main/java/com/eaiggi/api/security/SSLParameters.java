package com.eaiggi.api.security;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude={"clientKeyPassword", "clientKeyStorePassword", "caTrustStorePassword"})
public class SSLParameters {
	private String protocol;
	@NotNull
	private String clientKeyStoreFilePath;
	private String clientKeyStorePassword;
	@NotNull
	private String clientKeyPassword;
	@NotNull
	private String caTrustStoreFilePath;
	private String caTrustStorePassword;
}
