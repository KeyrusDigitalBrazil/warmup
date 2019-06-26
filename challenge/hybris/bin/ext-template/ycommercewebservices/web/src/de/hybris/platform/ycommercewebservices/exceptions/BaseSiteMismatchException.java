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
package de.hybris.platform.ycommercewebservices.exceptions;

import javax.servlet.ServletException;


public class BaseSiteMismatchException extends ServletException
{
	public BaseSiteMismatchException(final String baseSiteIdInRequest, final String baseSiteIdInCart)
	{
		super("Base site '" + baseSiteIdInRequest + "' from the current request does not match with base site '" + baseSiteIdInCart
				+ "' from the cart!");
	}
}
