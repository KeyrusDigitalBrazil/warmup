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
package com.hybris.cis.client.shared.exception.codes;

/**
 * Web exception codes (range 9000 - 9999).
 */
public enum WebServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * "Exception during unmarshalling".
	 */
	UNMARSHALL_EXCEPTION(9000, "Exception during unmarshalling"),

	/**
	 * "Client reference ID not found".
	 */
	MISSING_CLIENT_REF_ID(9001, "Client reference ID not found");


	private final int code;
	private final String message;

	private WebServiceExceptionCodes(final int code, final String message)
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
