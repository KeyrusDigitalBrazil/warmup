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

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;


/**
 * A default strategy for commerce cart calculation with external tax calculation.
 * 
 * @deprecated Since 6.0. Use DefaultCommerceCartCalculationStrategy with calculateExternalTax to true instead
 */
@Deprecated
public class DefaultCheckoutCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy
{

	@Override
	public boolean calculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		final boolean calculated = super.calculateCart(parameter);
		getExternalTaxesService().calculateExternalTaxes(cartModel);
		return calculated;
	}



}
