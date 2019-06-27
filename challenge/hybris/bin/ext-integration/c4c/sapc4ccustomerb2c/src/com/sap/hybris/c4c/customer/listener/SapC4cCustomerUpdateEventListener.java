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
package com.sap.hybris.c4c.customer.listener;

import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;


/**
 * Catch the customer update event and publish to SCPI
 */
public class SapC4cCustomerUpdateEventListener extends AbstractEventListener<SapC4cCustomerUpdateEvent>
{

	private SapC4cCustomerPublicationService c4cCustomerPublicationService;
	private static final Logger LOGGER = LogManager.getLogger(SapC4cCustomerUpdateEventListener.class);


	@Override
	protected void onEvent(final SapC4cCustomerUpdateEvent event)
	{

		final C4CCustomerData customerData = event.getCustomerData();
		try
		{
			getC4cCustomerPublicationService().publishCustomerToCloudPlatformIntegration(customerData);
		}
		catch (final IOException e)
		{
			LOGGER.error("Failed to replicate customer " + customerData.getCustomerId(), e);
		}
	}


	/**
	 * @return the c4cCustomerPublicationService
	 */
	public SapC4cCustomerPublicationService getC4cCustomerPublicationService()
	{
		return c4cCustomerPublicationService;
	}


	/**
	 * @param c4cCustomerPublicationService the c4cCustomerPublicationService to set
	 */
	public void setC4cCustomerPublicationService(final SapC4cCustomerPublicationService c4cCustomerPublicationService)
	{
		this.c4cCustomerPublicationService = c4cCustomerPublicationService;
	}

}
