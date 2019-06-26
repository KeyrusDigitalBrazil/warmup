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
package com.hybris.cis.client.shipping.exception;


import com.hybris.cis.client.shared.exception.codes.StandardServiceExceptionCode;

/**
 * Shipping api service exception codes (range 5000 - 5999).
 */
public enum ShippingServiceExceptionCodes implements StandardServiceExceptionCode
{

	/**
	 * "The ship to address is invalid".
	 */
	SHIP_TO_ADDRESS_INVALID(5000, "The ship to address is invalid"),

	/**
	 * "The ship to address is missing".
	 */
	SHIP_TO_ADDRESS_MISSING(5001, "The ship to address is missing"),

	/**
	 * "The ship from address is invalid".
	 */
	SHIP_FROM_ADDRESS_INVALID(5002, "The ship from address is invalid"),

	/**
	 * "The ship from address is missing".
	 */
	SHIP_FROM_ADDRESS_MISSING(5003, "The ship from address is missing"),

	/**
	 * "The package is missing".
	 */
	PACKAGE_MISSING(5004, "The package is missing"),

	/**
	 * "The dimensions of the package is invalid".
	 */
	PACKAGE_DIMENSIONS_INVALID(5005, "The dimensions of the package is invalid"),

	/**
	 * "Shipping method is invalid".
	 */
	SHIPPING_METHOD_INVALID(5006, "Shipping method is invalid"),

	/**
	 * "Shipping method is missing".
	 */
	SHIPPING_METHOD_MISSING(5007, "Shipping method is missing"),

	/**
	 * "The service used does not accept the specified weight unit".
	 */
	INCOMPATIBLE_WEIGHT_UNIT(5008, "The service used does not accept the specified weight unit"),

	/**
	 * "The content type is missing".
	 */
	CONTENT_TYPE_MISSING(5009, "The content type is missing"),

	/**
	 * "The delivery confirmation service is not available for this service method".
	 */
	DELIVERY_SERVICE_NOT_AVAILABLE_FOR_THIS_SERVICE_METHOD(5010,
			"The delivery confirmation service is not available for this service method"),

	/**
	 * "The Shippers shipper number cannot be used for the shipment".
	 */
	SHIPPER_NUMBER_INVALID(5011, "The Shippers shipper number cannot be used for the shipment"),

	/**
	 * "The type is not set on the package".
	 */
	PACKAGE_TYPE_MISSING(5012, "The package type is not set");

	private final int code;
	private final String message;

	private ShippingServiceExceptionCodes(final int code, final String message)
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
