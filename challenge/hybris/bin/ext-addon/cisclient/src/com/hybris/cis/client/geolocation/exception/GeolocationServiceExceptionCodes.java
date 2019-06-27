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
package com.hybris.cis.client.geolocation.exception;


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCode;

/**
 * This class specifies the excpetion codes for the CIS geolocation service.
 * The code range for geolocation is 8000 - 9000.
 */
public enum GeolocationServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * Address is missing.
	 */
	ADDRESS_NOT_SET(8000, "The address was not set."),

	/**
	 * Country is missing.
	 */
	COUNTRY_NOT_SPECIFIED(8001, "The country was not specified in the request"),

	/**
	 * Zipcode is missing.
	 */
	ZIPCODE_NOT_SPECIFIED(8002, "The zipcode was not specified in the request");


	private final int code;
	private final String message;

	private GeolocationServiceExceptionCodes(final int code, final String message)
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
