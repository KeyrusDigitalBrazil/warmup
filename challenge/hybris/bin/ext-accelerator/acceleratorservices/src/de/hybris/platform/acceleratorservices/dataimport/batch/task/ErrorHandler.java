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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchException;

import org.apache.log4j.Logger;
import org.springframework.messaging.MessagingException;


/**
 * Error Handler to trigger a cleanup.
 */
public class ErrorHandler
{
	private static final Logger LOG = Logger.getLogger(ErrorHandler.class);

	private CleanupHelper cleanupHelper;

	/**
	 * Point of entry for errors during processing routed to the error channel.
	 * 
	 * @param exception
	 */
	public void onError(final MessagingException exception)
	{
		LOG.error("unexpected exception caught", exception);
		if (exception.getCause() instanceof BatchException)
		{
			cleanupHelper.cleanup(((BatchException) exception.getCause()).getHeader(), true);
		}
	}


	/**
	 * Point of entry for errors during processing routed to the error channel.
	 * 
	 * @param exception
	 */
	public void onError(final IllegalStateException exception)
	{
		LOG.error("unexpected exception caught", exception);
		if (exception.getCause() instanceof BatchException)
		{
			cleanupHelper.cleanup(((BatchException) exception.getCause()).getHeader(), true);
		}
	}


	/**
	 * @param cleanupHelper
	 *           the cleanupHelper to set
	 */
	public void setCleanupHelper(final CleanupHelper cleanupHelper)
	{
		this.cleanupHelper = cleanupHelper;
	}

	/**
	 * 
	 * @return {@link CleanupHelper} instance.
	 */
	protected CleanupHelper getCleanupHelper()
	{
		return cleanupHelper;
	}

}
