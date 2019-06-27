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
package de.hybris.platform.b2bacceleratorservices.order.checkout.flow;

import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;



/**
 * Abstraction for strategy determining flow for checkout logic.
 *
 * @since 4.6
 */
public interface B2BCheckoutFlowStrategy
{
	/**
	 * Returns one of the possible {@link CheckoutFlowEnum} values - to select the checkout flow
	 */
	CheckoutFlowEnum getCheckoutFlow();
}
