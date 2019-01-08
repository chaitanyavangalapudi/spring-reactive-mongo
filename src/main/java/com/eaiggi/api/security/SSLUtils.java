package com.eaiggi.api.security;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.eaiggi.api.exception.BaseException;
import com.eaiggi.api.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SSLUtils {
	private static final String TLS = "TLS";	

	public static SSLContext createSslContext(SSLParameters sslParameters) throws BaseException {
		log.debug("Creating SSLContext with Parameters:{}", sslParameters);
		if (sslParameters == null) {
			throw new BaseException("SSL Parameters Can't be NULL");
		}
		try {
			SSLContext sslContext = SSLContext.getInstance(TLS);			
			TrustManagerFactory trustManagerFactory = getTrustManagerFactory(sslParameters.getCaTrustStoreFilePath(),
					sslParameters.getCaTrustStorePassword());
			KeyManagerFactory keyManagerFactory = getKeyManagerFactory(sslParameters.getClientKeyStoreFilePath(),
					sslParameters.getClientKeyStorePassword());
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
					new SecureRandom());
			return sslContext;
		} catch (Exception e) {			
			log.error("Unable to initialize SSL Context", e);
			throw new BaseException("Unable to initialize SSL Context", e);
		}
	}

	private static KeyStore getKeyStore(String keyStoreFile, String passwd) throws BaseException {
		KeyStore keyStore = null;
		try {
			FileInputStream keyStoreFileStream = new FileInputStream(keyStoreFile);
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(keyStoreFileStream, passwd.toCharArray());
		} catch (Exception e) {
			throw new BaseException("Error while loading KeyStore from File:" + keyStoreFile, e);
		}
		return keyStore;
	}

	@SuppressWarnings("unused")
	private static KeyStore getKeyStoreX509(String fileName, String alias) {
		KeyStore keyStore = null;
		final String certificateType = "X.509";
		String tAlias = (alias.isEmpty() ? "CA" : alias);
		try {
			CertificateFactory cf = CertificateFactory.getInstance(certificateType);
			InputStream inputStream = new FileInputStream(fileName);
			Certificate ca;
			try {
				ca = cf.generateCertificate(inputStream);
				log.debug("ca={}", ((X509Certificate) ca).getSubjectDN());
			} finally {
				inputStream.close();
			}

			String keyStoreType = KeyStore.getDefaultType();
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry(tAlias, ca);
		} catch (Exception e) {
			log.error("Error while loading keystore", e);
		}
		return keyStore;
	}

	private static KeyManagerFactory getKeyManagerFactory(String certPath, String certPasswd) throws BaseException {
		if (!CommonUtils.isValidFile(certPath))
			throw new BaseException("Unable to access Key Store File:" + certPath);
		log.debug("Processing KeyStore File:{} ....", certPath);
		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			KeyStore keyStore = SSLUtils.getKeyStore(certPath, certPasswd);
			kmf.init(keyStore, certPasswd.toCharArray());
		} catch (Exception e) {
			throw new BaseException("Error during getting Key store", e);			
		}
		return kmf;
	}

	private static TrustManagerFactory getTrustManagerFactory(String certPath, String certPasswd) throws BaseException {
		if (!CommonUtils.isValidFile(certPath))
			throw new BaseException("Unable to access Trust Store File:" + certPath);
		log.debug("Processing TrustStore File:{} ....", certPath);
		KeyStore keyStore = getKeyStore(certPath, certPasswd);
		TrustManagerFactory trustManagerFactory = null;
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
		} catch (Exception e) {
			throw new BaseException("Error during getting Trust store", e);
		}
		return trustManagerFactory;
	}
}
