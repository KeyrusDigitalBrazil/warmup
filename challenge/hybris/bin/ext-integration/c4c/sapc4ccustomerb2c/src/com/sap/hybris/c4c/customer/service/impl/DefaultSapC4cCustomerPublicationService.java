/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.service.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;


/**
 * Service for publishing Customer JSON to SCPI
 */
public class DefaultSapC4cCustomerPublicationService implements SapC4cCustomerPublicationService
{

	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;
	private ConfigurationService configurationService;

	/**
	 * Publishes Customer Data to SCPI
	 */
	@Override
	public void publishCustomerToCloudPlatformIntegration(final C4CCustomerData customerJson) throws IOException
	{
		getCloudPlatformIntegrationConnection().sendPost(
				getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_SCPI_IFLOW_KEY),
				customerJson.toString());
	}

	/**
	 * @return the cloudPlatformIntegrationConnection
	 */
	public CloudPlatformIntegrationConnection getCloudPlatformIntegrationConnection()
	{
		return cloudPlatformIntegrationConnection;
	}


	/**
	 * @param cloudPlatformIntegrationConnection
	 *           the cloudPlatformIntegrationConnection to set
	 */
	public void setCloudPlatformIntegrationConnection(final CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection)
	{
		this.cloudPlatformIntegrationConnection = cloudPlatformIntegrationConnection;
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
