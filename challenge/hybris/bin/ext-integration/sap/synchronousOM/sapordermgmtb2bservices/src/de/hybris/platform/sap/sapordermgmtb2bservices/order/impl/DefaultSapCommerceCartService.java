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
package de.hybris.platform.sap.sapordermgmtb2bservices.order.impl;


import de.hybris.platform.commerceservices.order.CommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;


/**
 * Default Sap Commerce Cart Service
 */
public class DefaultSapCommerceCartService extends DefaultCommerceCartService
{
	private CommerceCartRestorationStrategy commerceCartRestorationStrategy;

	/**
	 * @return the commerceCartRestorationStrategy
	 */
	@Override
	public CommerceCartRestorationStrategy getCommerceCartRestorationStrategy()
	{
		return commerceCartRestorationStrategy;
	}

	/**
	 * @param commerceCartRestorationStrategy
	 *           the commerceCartRestorationStrategy to set
	 */
	@Override
	public void setCommerceCartRestorationStrategy(final CommerceCartRestorationStrategy commerceCartRestorationStrategy)
	{
		this.commerceCartRestorationStrategy = commerceCartRestorationStrategy;
	}
}

