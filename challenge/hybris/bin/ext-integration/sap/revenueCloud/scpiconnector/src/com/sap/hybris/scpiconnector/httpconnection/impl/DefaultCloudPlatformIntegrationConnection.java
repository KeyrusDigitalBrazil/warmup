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
package com.sap.hybris.scpiconnector.httpconnection.impl;

import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.io.IOException;
import java.util.Optional;

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

import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;
import com.sap.hybris.scpiconnector.model.SAPCPIConfigurationModel;



/**
 *
 */
public class DefaultCloudPlatformIntegrationConnection implements CloudPlatformIntegrationConnection
{
	private static final Logger LOGGER = LogManager.getLogger(DefaultCloudPlatformIntegrationConnection.class);
	private GenericDao sapCPIConfigurationModelGenericDao;

	@Override
	public ResponseData sendPost(final String iflowKey, final Object body) throws IOException
	{
		final ResponseData resData = new ResponseData();
		final SAPCPIConfigurationModel config = getSAPCPIConfiguration();
		if (config == null || StringUtils.isEmpty(iflowKey))
		{
			LOGGER.info("Could not connect to CPI. Configuration Missing.");
		}
		else
		{
			config.getIflowConfiguration().stream().filter(conf -> iflowKey.equals(conf.getIflowKey())).findFirst()
					.ifPresent(iflowConfig -> {
						final Response resObj = publish(config.getBaseUrl(), iflowConfig.getIflowUrl(), config.getProxyUrl(), body,
								config.getUsername(), config.getPassword());
						populateResponseData(resObj, resData);
					});
			;
		}
		return resData;

	}


	/**
	 *
	 */
	private void populateResponseData(final Response resObj, final ResponseData resData)
	{
		resData.setStatus(String.valueOf(resObj.getStatus()));
		resData.setResponseContent(resObj.readEntity(String.class));

	}


	protected Response publish(final String baseUrl, final String pathUrl, final String proxyUrl, final Object body,
			final String username, final String password)
	{
		final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(username, password).build();
		final ClientConfig config = new ClientConfig();
		Response response = null;
		config.connectorProvider(new ApacheConnectorProvider());
		if (!StringUtils.isEmpty(proxyUrl))
		{
			config.property(ClientProperties.PROXY_URI, proxyUrl);
		}
		final Client client = ClientBuilder.newClient(config);
		client.register(feature);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Request Body:" + body);
		}
		try
		{
			final WebTarget webtarget = client.target(baseUrl).path(pathUrl);
			final Invocation.Builder invocationBuilder = webtarget.request(MediaType.APPLICATION_JSON);
			response = invocationBuilder.post(Entity.entity(body, MediaType.APPLICATION_JSON));
			LOGGER.info("Result:" + response);
		}
		catch (final IllegalArgumentException e)
		{
			LOGGER.warn("Hostname not null due to url not present for Publication");
		}
		return response;

	}

	protected SAPCPIConfigurationModel getSAPCPIConfiguration()
	{
		final Optional<SAPCPIConfigurationModel> configOpt = getSapCPIConfigurationModelGenericDao().find().stream().findFirst();
		return configOpt.orElse(null);
	}


	/**
	 * @return the sapCPIConfigurationModelGenericDao
	 */
	public GenericDao getSapCPIConfigurationModelGenericDao()
	{
		return sapCPIConfigurationModelGenericDao;
	}


	/**
	 * @param sapCPIConfigurationModelGenericDao
	 *           the sapCPIConfigurationModelGenericDao to set
	 */
	public void setSapCPIConfigurationModelGenericDao(final GenericDao sapCPIConfigurationModelGenericDao)
	{
		this.sapCPIConfigurationModelGenericDao = sapCPIConfigurationModelGenericDao;
	}



}
