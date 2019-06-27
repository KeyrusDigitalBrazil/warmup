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
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.marketplaceservices.strategies.VendorOrderTotalPriceCalculationStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;


public class DefaultVendorOrderTotalPriceCalculationStrategy implements VendorOrderTotalPriceCalculationStrategy
{

	@Override
	public double calculateTotalPrice(ConsignmentModel consignment)
	{
		if (consignment.getConsignmentEntries() == null)
		{
			return 0;
		}
		else
		{
			return consignment.getConsignmentEntries().stream().map(ConsignmentEntryModel::getOrderEntry)
					.mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum();
		}
	}

}