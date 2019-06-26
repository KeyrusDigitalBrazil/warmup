/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.kymaintegrationservices.utils;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.utils.SecurityUtils;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Wrapper for RestTemplate to handle ssl connections
 */
public class RestTemplateWrapper
{
	private static final String MAX_REDIRECTS = "kymaintegrationservices.max_redirects";

	private static final Logger LOG = LoggerFactory.getLogger(RestTemplateWrapper.class);
	private RestTemplate restTemplate;
	private DestinationService<AbstractDestinationModel> destinationService;
	private int timeout;

	private Map<AbstractCredentialModel, HttpComponentsClientHttpRequestFactory> clientFactoryCash = new ConcurrentHashMap<>();

	public RestTemplate getUpdatedRestTemplate()
	{
		return restTemplate;
	}

	public void updateCredentials(final AbstractDestinationModel destination) throws CredentialException
	{
		validateCredential(destination);
		if (clientFactoryCash.containsKey(destination.getCredential()))
		{
			final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = clientFactoryCash
					.get(destination.getCredential());
			getRestTemplate().setRequestFactory(httpComponentsClientHttpRequestFactory);
		}
		else
		{
			updateRequestFactory((ConsumedCertificateCredentialModel) destination.getCredential());
		}
	}

	public void invalidateAndUpdateCache(final ConsumedCertificateCredentialModel credential)
	{
		try
		{
			final HttpComponentsClientHttpRequestFactory requestFactory = clientFactoryCash.remove(credential);
			if (requestFactory != null)
			{
				requestFactory.destroy();
			}
			updateRequestFactory(credential);
		}
		catch (final Exception e)
		{
			LOG.error(String.format("Something bad happen during cache invalidation during credentials update, cause: %s", e.getMessage()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}
		}
	}

	protected void updateRequestFactory(final ConsumedCertificateCredentialModel credential) throws CredentialException
	{
		final String randomString = UUID.randomUUID().toString();

		byte[] certBytes = new byte[0];
		byte[] keyBytes = new byte[0];

		try
		{
			final SSLContext context = SSLContext.getInstance("TLS");

			certBytes = DatatypeConverter.parseBase64Binary(credential.getCertificateData());
			keyBytes = DatatypeConverter.parseBase64Binary(credential.getPrivateKey());

			final X509Certificate cert = SecurityUtils.generateCertificateFromDER(certBytes);
			final RSAPrivateKey key = SecurityUtils.generatePrivateKeyFromDER(keyBytes);

			final KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(null);
			keystore.setCertificateEntry(randomString, cert);
			keystore.setKeyEntry(randomString, key, randomString.toCharArray(), new Certificate[]
			{ cert });

			final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, randomString.toCharArray());
			context.init(kmf.getKeyManagers(), null, null);

			final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(Config.getInt(MAX_REDIRECTS, 10)).build();

			final HttpClient client = HttpClients.custom().setSSLContext(context)
					.setKeepAliveStrategy((httpResponse, httpContext) -> getKeepAlive()).setDefaultRequestConfig(requestConfig)
					.setConnectionTimeToLive(getKeepAlive(), TimeUnit.MILLISECONDS).build();
			final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
					client);
			clientHttpRequestFactory.setConnectTimeout(timeout);

			clientFactoryCash.put(credential, clientHttpRequestFactory);

			getRestTemplate().setRequestFactory(clientHttpRequestFactory);
		}
		catch (final NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException
				| CertificateException | IOException | CredentialException e)
		{
			LOG.error(e.getMessage());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}
			throw new CredentialException(String.format("Invalid Certificate Credential with id: [{%s}]", credential.getId()), e);
		}
		finally
		{
			Arrays.fill(certBytes, (byte) 0);
			Arrays.fill(keyBytes, (byte) 0);
		}
	}

	protected RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	protected int getKeepAlive()
	{
		return Config.getInt("kymaintegrationservices.connections.keep-alive", 60000);
	}

	@Required
	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	protected void validateCredential(final AbstractDestinationModel destination) throws CredentialException
	{
		if (!(destination.getCredential() instanceof ConsumedCertificateCredentialModel))
		{
			final String errorMessage = "Missing Consumed Certificate Credential. Please get a client certificate from Kyma.";
			LOG.error(errorMessage);
			throw new CredentialException(errorMessage);
		}

		final ConsumedCertificateCredentialModel credential = (ConsumedCertificateCredentialModel) destination.getCredential();

		if (StringUtils.isEmpty(credential.getCertificateData()) || StringUtils.isEmpty(credential.getPrivateKey()))
		{
			final String errorMessage = String.format("Invalid Certificate Credential with id: [{%s}]", credential.getId());
			LOG.error(errorMessage);
			throw new CredentialException(errorMessage);
		}
	}

	protected int getTimeout()
	{
		return timeout;
	}

	@Required
	public void setTimeout(final int timeout)
	{
		this.timeout = timeout;
	}
}
