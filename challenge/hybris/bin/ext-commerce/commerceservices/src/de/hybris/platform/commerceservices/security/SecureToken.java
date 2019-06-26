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
package de.hybris.platform.commerceservices.security;

/**
 * Token data for the SecureToken processor.
 */
public class SecureToken
{
	private final String data;
	private final long timeStamp;

	public SecureToken(final String data, final long timeStamp)
	{
		this.data = data;
		this.timeStamp = timeStamp;
	}

	public String getData()
	{
		return data;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}
}
