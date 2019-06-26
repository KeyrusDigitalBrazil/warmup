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
package com.hybris.cis.client.fraud.exception;


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCode;

/**
 * Fraud api service exception codes (range 10000 - 11000).
 */
public enum FraudServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * "The order status update is not valid".
	 */
	INVALID_ORDER_STATUS_UPDATE(10000, "The order status update is not valid"),

	/**
	 * "The address type is not valid".
	 */
	INVALID_ADDRESS_TYPE(10001, "Address type not valid"),

	/**
	 * "The service responded with an invalid fraud status".
	 */
	INVALID_FRAUD_STATUS(10002, "The service responded with an invalid fraud status.");

	private final int code;
	private final String message;

	private FraudServiceExceptionCodes(final int code, final String message)
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
