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
package de.hybris.platform.sap.sapinvoiceaddon.exception;

/**
 * Thrown if something wrong in invoice details.
 *
 */
public class SapInvoiceException extends Exception
{

	private static final long serialVersionUID = -6529406933930670212L;

	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 */
	public SapInvoiceException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 *
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public SapInvoiceException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
