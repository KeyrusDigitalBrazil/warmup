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
package de.hybris.platform.chinesepaymentservices.stocklevel.dao;

import de.hybris.platform.chinesepaymentservices.model.StockLevelReservationHistoryEntryModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * The Dao of StockLevelReservationHistoryEntry
 */
public interface StockLevelReservationHistoryEntryDao extends Dao
{
	/**
	 * Search the StockLevelReservationHistoryEntry by ordercode in the database
	 *
	 * @param orderCode
	 *           The code of the order
	 * @return List<StockLevelReservationHistoryEntryModel>
	 */
	List<StockLevelReservationHistoryEntryModel> getStockLevelReservationHistoryEntryByOrderCode(String orderCode);
}
