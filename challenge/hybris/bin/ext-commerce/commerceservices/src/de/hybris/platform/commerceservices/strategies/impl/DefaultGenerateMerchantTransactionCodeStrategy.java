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
package de.hybris.platform.commerceservices.strategies.impl;

import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;

import java.util.UUID;


public class DefaultGenerateMerchantTransactionCodeStrategy implements GenerateMerchantTransactionCodeStrategy
{
	@Override
	public String generateCode(final CartModel cartModel)
	{
		return cartModel.getUser().getUid() + "-" + UUID.randomUUID();
	}

}
