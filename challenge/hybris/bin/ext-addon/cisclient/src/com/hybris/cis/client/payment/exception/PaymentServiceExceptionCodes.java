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
package com.hybris.cis.client.payment.exception;


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCode;

/**
 * Payment api service exception codes (range 4000 - 4999).
 */
public enum PaymentServiceExceptionCodes implements StandardServiceExceptionCode
{
	/**
	 * "The card type is invalid".
	 */
	CARD_TYPE_INVALID(4000, "The card type is invalid"),

	/**
	 * "The card number is invalid".
	 */
	CARD_ACCOUNTNUMBER_INVALID(4001, "The card number is invalid"),

	/**
	 * The expiration date is invalid".
	 */
	CARD_EXP_DATE_INVALID(4002, "The expiration date is invalid"),

	/**
	 * "The amount requested is invalid".
	 */
	AMOUNT_INVALID(4003, "The amount requested is invalid"),

	/**
	 * "The amount requested is missing".
	 */
	AMOUNT_MISSING(4004, "The amount requested is missing"),

	/**
	 * "The card type is missing".
	 */
	CARD_TYPE_MISSING(4005, "The card type is missing"),

	/**
	 * "The card number is missing".
	 */
	CARD_ACCOUNTNUMBER_MISSING(4006, "The card number is missing"),

	/**
	 * "The expiration date is missing".
	 */
	CARD_EXP_DATE_MISSING(4007, "The expiration date is missing"),

	/**
	 * "Signature verification failed for request".
	 */
	SIGNATURE_VERIFICATION_FAILED(4008, "Signature verification failed for request"),

	/**
	 * "The order status update is not valid".
	 */
	INVALID_ORDER_STATUS_UPDATE(4009, "The order status update is not valid"),

	/**
	 * "The capture type is not valid".
	 */
	INVALID_CAPTURE_TYPE(4010, "The capture type is not valid"),

	/**
	 * "The customer email is missing".
	 */
	MISSING_CUSTOMER_EMAIL(4011, "The customer email is missing"),
	/**
	 * "The billing address is invalid".
	 */
	INVALID_BILLING_ADDRESS(4012, "The customer billing address is invalid"),
	/**
	 * "An unhandled uncritical error code was returned by the psp ".
	 */
	UNKNOWN_UNCRITICAL_ERROR(4013, "Unhandled not critical error was returned by the psp"),
	/**
	 * "An unhandled critical error code was returned by the psp ".
	 */
	UNKNOWN_CRITICAL_ERROR(4014, "Unhandled critical error was returned by the psp"),
	/**
	 * "An unhandled critical error code was returned by the psp ".
	 */
	GENERAL_DECLINE_OF_CARD(4015, "Card was declined by bank with no further information provided"),
	/**
	 * "The CV number was rejected.".
	 */
	CVV_NUMBER_REJECTED(4016, "The CV number was rejected or missing."),
    /**
     * "Unreadable request was send".
     */
    UNREADABLE_REQUEST(4017, "The request you send couldn't be parsed");

	private final int code;
	private final String message;

	private PaymentServiceExceptionCodes(final int code, final String message)
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
