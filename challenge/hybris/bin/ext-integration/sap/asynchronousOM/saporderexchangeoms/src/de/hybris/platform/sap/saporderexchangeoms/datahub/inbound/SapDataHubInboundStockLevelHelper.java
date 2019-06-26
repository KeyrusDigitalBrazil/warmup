/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound;

import de.hybris.platform.jalo.Item;

/**
 * OMS data hub inbound helper for stock level change notifications
 */
public interface SapDataHubInboundStockLevelHelper
{
	/**
	 * After the stock replication from ERP, we have one of the following scenarios:
	 * 1. Not shipped allocations + hybris ATP >  ERP Stock Level  -> Increase inventory event (-the difference)
	 * 2. Not shipped allocations + hybris ATP <  ERP Stock Level  -> Increase inventory event (the difference)
	 * 3. Not shipped allocations + hybris ATP =  ERP Stock Level  -> Do not do anything
	 * @param stockLevelQuantity
	 * @param stockLevelItem
	 */
	void processStockLevelNotification(String stockLevelQuantity, Item stockLevelItem);

}