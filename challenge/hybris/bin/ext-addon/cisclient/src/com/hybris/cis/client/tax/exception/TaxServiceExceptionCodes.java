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
package com.hybris.cis.client.tax.exception;


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCode;

/**
 * The standard exception codes for tax services (Range 3000 - 3999).
 */
public enum TaxServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * "Ship to address missing".
	 */
	SHIP_TO_MISSING(3000, "Ship to address missing"),
	/**
	 * "Taxes could not be calculated, the 3rd party servire returned an error".
	 */
	TAXES_NOT_CALCULATED(3001, "Taxes could not be calculated, the 3rd party servire returned an error");

	/** The exception code. */
	private final int code;
	/** The exception message. */
	private final String message;

	private TaxServiceExceptionCodes(final int code, final String message)
	{
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode()
	{
		return this.code;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

	@Override
	public String toString()
	{
		return this.code + " - " + this.message;
	}

}
