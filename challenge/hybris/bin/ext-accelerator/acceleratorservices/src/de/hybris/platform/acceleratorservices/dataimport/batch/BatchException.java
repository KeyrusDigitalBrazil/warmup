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
package de.hybris.platform.acceleratorservices.dataimport.batch;

import de.hybris.platform.servicelayer.exceptions.SystemException;


/**
 * Batch Exception encapsulating the BatchHeader information.
 */
public class BatchException extends SystemException // NOSONAR
{
	private final BatchHeader header; // NOSONAR

	/**
	 * @param message
	 */
	public BatchException(final String message)
	{
		super(message);
		this.header = null;
	}

	/**
	 * @param message
	 * @param header
	 */
	public BatchException(final String message, final BatchHeader header)
	{
		super(message);
		this.header = header;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BatchException(final String message, final Throwable cause)
	{
		super(message, cause);
		this.header = null;
	}

	/**
	 * @param message
	 * @param header
	 * @param cause
	 */
	public BatchException(final String message, final BatchHeader header, final Throwable cause)
	{
		super(message, cause);
		this.header = header;
	}

	/**
	 * @param cause
	 */
	public BatchException(final Throwable cause)
	{
		super(cause);
		this.header = null;
	}

	/**
	 * @return the header
	 */
	public BatchHeader getHeader()
	{
		return header;
	}
}
