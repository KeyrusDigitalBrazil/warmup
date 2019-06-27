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
package de.hybris.platform.stocknotificationoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * Exception definition for Stock Notification
 */
public class StockNotificationException extends WebserviceException
{
	public static final String NORMAL_PRODUCT = "normalProduct";
	private static final String TYPE = "StockNotificationError";
	private static final String SUBJECT_TYPE = "stocknotifacition";

	public StockNotificationException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}
}
