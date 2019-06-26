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
package de.hybris.platform.b2bacceleratorservices.order.checkout.pci.impl;

import de.hybris.platform.b2bacceleratorservices.order.checkout.pci.B2BCheckoutPciStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public abstract class AbstractB2BCheckoutPciStrategy implements B2BCheckoutPciStrategy
{
	private B2BCheckoutPciStrategy defaultCheckoutPciStrategy;


	protected B2BCheckoutPciStrategy getDefaultCheckoutPciStrategy()
	{
		return this.defaultCheckoutPciStrategy;
	}

	@Required
	public void setDefaultCheckoutPciStrategy(final B2BCheckoutPciStrategy defaultCheckoutPciStrategy)
	{
		this.defaultCheckoutPciStrategy = defaultCheckoutPciStrategy;
	}
}
