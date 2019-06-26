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

import de.hybris.platform.commerceservices.externaltax.DecideExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.springframework.util.Assert;


/**
 * An implementation of {@link DecideExternalTaxesStrategy} to determine whether or not a recalculation of the taxes is
 * required.
 */
public class DefaultCisDetermineExternalTaxStrategy implements DecideExternalTaxesStrategy
{

	@Override
	public boolean shouldCalculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		Assert.notNull(abstractOrder, "Order is null. Cannot apply external tax to it.");

		return (Boolean.TRUE.equals(abstractOrder.getNet())
				&& Boolean.TRUE.equals(abstractOrder.getStore().getExternalTaxEnabled()) && abstractOrder.getDeliveryMode() != null && abstractOrder.getDeliveryAddress() != null);
	}
}
