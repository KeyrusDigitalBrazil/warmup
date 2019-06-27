/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.monitoring;

/**
 * An InboundMonitoringException represents an exception that occurs during monitoring processing
 */
public class InboundMonitoringException extends RuntimeException
{
	public InboundMonitoringException(final Throwable cause)
	{
		super(cause);
	}
}
