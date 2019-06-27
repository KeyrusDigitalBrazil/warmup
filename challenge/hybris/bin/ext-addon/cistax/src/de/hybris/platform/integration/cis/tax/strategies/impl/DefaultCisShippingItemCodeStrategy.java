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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.strategies.ShippingItemCodeStrategy;
import java.util.List;


/**
 * Default implementation of {@link ShippingItemCodeStrategy} which will return one greater than last entry number.
 */
public class DefaultCisShippingItemCodeStrategy implements ShippingItemCodeStrategy
{
	@Override
	public Integer getShippingItemCode(final AbstractOrderModel abstractOrder)
	{
		final List<AbstractOrderEntryModel> orderEntries = abstractOrder.getEntries();
		return orderEntries.isEmpty() ? Integer.valueOf(0) : Integer.valueOf(orderEntries.get(orderEntries.size() - 1)
				.getEntryNumber().intValue() + 1);
	}
}
