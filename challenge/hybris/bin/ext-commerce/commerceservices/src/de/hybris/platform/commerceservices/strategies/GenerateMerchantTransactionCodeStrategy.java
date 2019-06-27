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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.platform.core.model.order.CartModel;


public interface GenerateMerchantTransactionCodeStrategy
{
	/**
	 * Generates a unique id for a {@link de.hybris.platform.payment.model.PaymentTransactionModel}
	 * @param cartModel A cart
	 * @return A unique identifier
	 */
	String generateCode(CartModel cartModel);
}
