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
package de.hybris.platform.stocknotificationoccaddon.constants;

/**
 * Class for error message constants.
 */
public final class ErrorMessageConstants
{

	public static final String NORMAL_PRODUCT_MESSAGE = "Stock available for this product, therefore this operation is not applicable.";
	public static final String NO_PRODUCT_INTEREST_MESSAGE = "There is no product interest subscribed for this product.";
	public static final String NO_MOBILE_BOUND_MESSAGE = "There is no bound mobile number.";
	public static final String NO_PRODUCT_FOUND_MESSAGE = "The product does not exist.";
	public static final String MISSING_CHANNEL_PARAMS_MESSAGE = "At least one channel is required.";
	public static final String INVALID_PREFERENCE_MESSAGE = "The notification channel '%s' is unknown.";
	public static final String INVALID_PARAMETER_MESSAGE = "The value for channel '%s' is invalid.";
	public static final String PARAMETER_PRODUCTCODE_REQUIRED_MESSAGE = "Required String parameter 'productCode' is not present.";

	public static final String SMS_PARAM_NAME = "SMS";
	public static final String PRODUCT_NOT_FOUND = "productNotFound";
	public static final String NO_PRODUCT_INTEREST = "noProductInterest";
	public static final String PARAMETER_PRODUCTCODE_REQUIRED = "noProductCode";

	private ErrorMessageConstants()
	{
		//empty to avoid instantiating this constant class
	}
}