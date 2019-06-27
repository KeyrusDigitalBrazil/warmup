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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;


/**
 * A strict strategy to add an entry to the cart. It throws an exception when there is not enough products in stock
 * 
 */
public class CommerceAddToCartStrictStrategy extends DefaultCommerceAddToCartStrategy
{
	@Override
	protected void validateAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		super.validateAddToCart(parameters);

		if (!isStockLevelSufficient(parameters.getCart(), parameters.getProduct(), parameters.getPointOfService(),
				parameters.getQuantity()))
		{
			throw new CommerceCartModificationException("Insufficient stock level");
		}
	}
}
