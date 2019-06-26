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
package de.hybris.platform.chinesepaymentservices.stocklevel.impl;

import de.hybris.platform.chinesepaymentservices.model.StockLevelReservationHistoryEntryModel;
import de.hybris.platform.chinesepaymentservices.stocklevel.StockLevelReservationHistoryEntryService;
import de.hybris.platform.chinesepaymentservices.stocklevel.dao.impl.ChineseStockLevelReservationHistoryEntryDao;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 *  Implementation for {@link StockLevelReservationHistoryEntryService}. Its main purpose is to get stocklevel reservation history entry.
 */
public class ChineseStockLevelReservationHistoryEntryService implements StockLevelReservationHistoryEntryService
{

	private ChineseStockLevelReservationHistoryEntryDao chineseStockLevelReservationHistoryEntryDao;

	@Override
	public List<StockLevelReservationHistoryEntryModel> getStockLevelReservationHistoryEntryByOrderCode(final String orderCode)
	{
		return chineseStockLevelReservationHistoryEntryDao.getStockLevelReservationHistoryEntryByOrderCode(orderCode);
	}

	protected ChineseStockLevelReservationHistoryEntryDao getChineseStockLevelReservationHistoryEntryDao()
	{
		return chineseStockLevelReservationHistoryEntryDao;
	}

	@Required
	public void setChineseStockLevelReservationHistoryEntryDao(
			final ChineseStockLevelReservationHistoryEntryDao chineseStockLevelReservationHistoryEntryDao)
	{
		this.chineseStockLevelReservationHistoryEntryDao = chineseStockLevelReservationHistoryEntryDao;
	}

}
