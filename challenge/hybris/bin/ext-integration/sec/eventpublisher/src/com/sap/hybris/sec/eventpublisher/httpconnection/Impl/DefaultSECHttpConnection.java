/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sec.eventpublisher.httpconnection.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.util.StringUtils;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.httpconnection.SECHttpConnection;



/**
 *
 */
public class DefaultSECHttpConnection implements SECHttpConnection
{
	private static final Logger LOGGER = LogManager.getLogger(DefaultSECHttpConnection.class);
	private ConfigurationService configurationService;

	@Override
	public ResponseData sendPost(final String pathUrl, final String body) throws IOException
	{

		final String baseUrl = getConfigurationService().getConfiguration().getString(EventpublisherConstants.BASE_URL);
		final String username = getConfigurationService().getConfiguration().getString(EventpublisherConstants.USERNAME);
		final String password = getConfigurationService().getConfiguration().getString(EventpublisherConstants.PASSWORD);
		final ResponseData resData = new ResponseData();

		final Response resObj = publish(baseUrl, pathUrl, body, username, password);
		populateResponseData(resObj, resData);
		return resData;

	}


	/**
	 *
	 */
	private void populateResponseData(final Response resObj, final ResponseData resData)
	{
		if (resObj != null)
		{
			resData.setStatus(String.valueOf(resObj.getStatus()));
			resData.setResponseContent(resObj.readEntity(String.class));
		}

	}


	public Response publish(final String baseUrl, final String pathUrl, final String body, final String username,
			final String password)
	{
		final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(username, password).build();
		final String PROXY_URL = getConfigurationService().getConfiguration().getString(EventpublisherConstants.SCPI_PROXY_URL);
		final ClientConfig config = new ClientConfig();
		Response response = null;
		config.connectorProvider(new ApacheConnectorProvider());
		if (!StringUtils.isEmpty(PROXY_URL))
		{
			config.property(ClientProperties.PROXY_URI, PROXY_URL);
		}
		final Client client = ClientBuilder.newClient(config);
		client.register(feature);
		
		LOGGER.debug("Request Body:" + body);

		try
		{
			if (!StringUtils.isEmpty(baseUrl))
			{
				final WebTarget webtarget = client.target(baseUrl).path(pathUrl);
				final Invocation.Builder invocationBuilder = webtarget.request(MediaType.APPLICATION_JSON);
				response = invocationBuilder.post(Entity.entity(body, MediaType.APPLICATION_JSON));
				LOGGER.info("Result:" + response);
			}
		}
		catch (final IllegalArgumentException e)
		{
			LOGGER.warn("Hostname not null due to url not present for Publication", e);
		}
		return response;

	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}







}
