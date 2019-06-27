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
package de.hybris.platform.cissapdigitalpayment.exceptions;

/**
 * Generic exception class to handle all the unspecified exceptions with SAP Digital payment
 */
public class SapDigitalPaymentServiceUnavaliableException extends RuntimeException
{
	private final String message;

	/**
	 * Constructor
	 */
	public SapDigitalPaymentServiceUnavaliableException(final String message)
	{
		super();
		this.message = message;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage()
	{
		return message;
	}





}
