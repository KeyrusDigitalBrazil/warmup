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
package de.hybris.platform.acceleratorfacades.flow.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.checkout.pci.CheckoutPciStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link CheckoutFlowFacade}.
 *
 * @since 4.6
 * @spring.bean checkoutFlowFacade
 */
public class DefaultCheckoutFlowFacade extends DefaultAcceleratorCheckoutFacade implements CheckoutFlowFacade
{

	private CheckoutPciStrategy checkoutPciStrategy;

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return getCheckoutPciStrategy().getSubscriptionPciOption();
	}

	protected CheckoutPciStrategy getCheckoutPciStrategy()
	{
		return this.checkoutPciStrategy;
	}

	@Required
	public void setCheckoutPciStrategy(final CheckoutPciStrategy strategy)
	{
		this.checkoutPciStrategy = strategy;
	}
}
