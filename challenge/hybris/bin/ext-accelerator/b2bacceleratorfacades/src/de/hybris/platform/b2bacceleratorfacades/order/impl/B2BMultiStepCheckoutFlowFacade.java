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
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * Mutli step checkout implementation of the {@link CheckoutFlowFacade} interface. Delegates resolving the checkout flow
 * to an injected {@link CheckoutFlowFacade}.
 */
public class B2BMultiStepCheckoutFlowFacade extends DefaultB2BAcceleratorCheckoutFacade implements CheckoutFlowFacade
{
	private CheckoutFlowFacade checkoutFlowFacade;

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return getCheckoutFlowFacade().getSubscriptionPciOption();
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	@Required
	public void setCheckoutFlowFacade(final CheckoutFlowFacade facade)
	{
		checkoutFlowFacade = facade;
	}
}
