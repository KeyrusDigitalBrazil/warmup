/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.commerceservices.order.impl;


import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

/**
 * Overrides {@code DefaultCommerceCartStrategy} to make bundle price rules working.
 */
public class BundleCommerceAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{
	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final CommerceCartModification modification = doAddToCart(parameter);
		afterAddToCart(parameter, modification);
		// It is required for price calculation of bundle entries.
		mergeEntry(modification, parameter);
		getCommerceCartCalculationStrategy().calculateCart(parameter);
		return modification;
	}
}