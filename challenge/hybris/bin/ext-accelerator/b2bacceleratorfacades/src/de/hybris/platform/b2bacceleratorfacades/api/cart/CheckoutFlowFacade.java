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
package de.hybris.platform.b2bacceleratorfacades.api.cart;

import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;



/**
 * The CheckoutFlowFacade supports resolving the {@link CheckoutFlowEnum} for the current request.
 */
public interface CheckoutFlowFacade
{
	/**
	 * Gets the checkout flow.
	 *
	 * @return the enum value of the checkout flow
	 */
	CheckoutFlowEnum getCheckoutFlow();

	/**
	 * Gets the subscription PCI Option.
	 *
	 * @return the enum value for subscription PCI Option
	 */
	CheckoutPciOptionEnum getSubscriptionPciOption();
}
