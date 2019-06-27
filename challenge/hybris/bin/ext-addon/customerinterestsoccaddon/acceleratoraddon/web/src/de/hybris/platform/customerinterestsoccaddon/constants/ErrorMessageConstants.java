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
package de.hybris.platform.customerinterestsoccaddon.constants;

/**
 * 
 */
public class ErrorMessageConstants
{
	public static final String NO_PRODUCT_FOUND_MESSAGE = "The product does not exist.";
	public static final String NO_PRODUCT_INTERESTS_FOUND_MESSAGE = "There is no product interest subscribed for this product.";
	public static final String NO_PRODUCT_FOUND = "productNotFound";
	public static final String NO_PRODUCT_INTERESTS = "noProductInterest";
	public static final String PAGESIZE_INVALID_MESSAGE = "The pagesize can not be less than or equal to 0.";
	public static final String PAGESIZE_INVALID = "invalidPageSize";

	private ErrorMessageConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
