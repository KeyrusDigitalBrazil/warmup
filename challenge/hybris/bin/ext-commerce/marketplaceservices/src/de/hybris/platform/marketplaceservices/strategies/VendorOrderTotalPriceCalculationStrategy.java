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
package de.hybris.platform.marketplaceservices.strategies;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;


/**
 * A strategy for vendor order total price calculation
 */
public interface VendorOrderTotalPriceCalculationStrategy
{
	/**
	 * Calculate total price of a given consignment
	 *
	 * @param consignment
	 *           given consignment
	 * @return total price of the consignment
	 */
	double calculateTotalPrice(ConsignmentModel consignment);
}
