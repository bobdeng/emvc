package com.handwin.util;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;



public class EasyX509TrustManager implements TrustManager,X509TrustManager {
	

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	private X509TrustManager standardTrustManager = null;


	/***************************************************************************
	 * Constructor for EasyX509TrustManager.
	 */
	public EasyX509TrustManager(KeyStore keystore)
			throws NoSuchAlgorithmException, KeyStoreException {
		super();
		TrustManagerFactory factory = TrustManagerFactory
				.getInstance("SunX509");
		factory.init(keystore);
		TrustManager[] trustmanagers = factory.getTrustManagers();
		if (trustmanagers.length == 0) {
			throw new NoSuchAlgorithmException(
					"SunX509 trust manager not supported");
		}
		this.standardTrustManager = (X509TrustManager) trustmanagers[0];
	}

	/*public boolean isServerTrusted(X509Certificate[] certificates) {
		if ((certificates != null) && LOG.isDebugEnabled()) {
			LOG.debug("Server certificate chain:");
			for (int i = 0; i < certificates.length; i++) {
				LOG.debug("X509Certificate[" + i + "]=" + certificates[i]);
			}
		}
		if ((certificates != null) && (certificates.length == 1)) {
			X509Certificate certificate = certificates[0];
			try {
				certificate.checkValidity();
			} catch (CertificateException e) {
				LOG.error(e.toString());
				return false;
			}
			return true;
		} else {
			return this.standardTrustManager.isServerTrusted(certificates);
		}
	}*/

	public X509Certificate[] getAcceptedIssuers() {
		return this.standardTrustManager.getAcceptedIssuers();
	}
}
