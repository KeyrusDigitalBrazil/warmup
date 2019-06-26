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
package de.hybris.platform.commerceservices.externaltax.impl;


import de.hybris.platform.commerceservices.externaltax.TaxAreaLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;


public class DefaultTaxAreaLookupStrategy implements TaxAreaLookupStrategy
{
	@Override
	public String getTaxAreaForOrder(final AbstractOrderModel orderModel)
	{
		if (orderModel == null || orderModel.getDeliveryAddress() == null)
		{
			throw new IllegalArgumentException("Can not determine taxArea for order without delivery address");
		}
		return orderModel.getDeliveryAddress().getCountry().getIsocode();
	}
}
