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
package de.hybris.platform.commerceservices.stock.strategies.impl;

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.Collection;


public class DefaultCommerceAvailabilityCalculationStrategy implements CommerceAvailabilityCalculationStrategy
{
	@Override
	public Long calculateAvailability(final Collection<StockLevelModel> stockLevels)
	{
		long totalActualAmount = 0;
		for (final StockLevelModel stockLevel : stockLevels)
		{
			// If any stock level is flagged as FORCEINSTOCK then return null to indicate in stock
			if (InStockStatus.FORCEINSTOCK.equals(stockLevel.getInStockStatus()))
			{
				return null;
			}

			// If any stock level is flagged as FORCEOUTOFSTOCK then we skip over it
			if (!InStockStatus.FORCEOUTOFSTOCK.equals(stockLevel.getInStockStatus()))
			{
				final long availableToSellQuantity = getAvailableToSellQuantity(stockLevel);
				if (availableToSellQuantity > 0 || !stockLevel.isTreatNegativeAsZero())
				{
					totalActualAmount += availableToSellQuantity;
				}
			}
		}
		return Long.valueOf(totalActualAmount);
	}

	/**
	 * Get the Available To Sell quantity for a StockLevel.
	 *
	 * Available To Sell stock consists of on-hand stock minus stock reserved for orders that have been placed and stock that have been backordered.
	 *
	 * @param stockLevel the stock level.
	 * @return the quantity available to sell.
	 */
	protected long getAvailableToSellQuantity(final StockLevelModel stockLevel)
	{
		return stockLevel.getAvailable() - stockLevel.getReserved() + (long) stockLevel.getOverSelling();
	}
}
