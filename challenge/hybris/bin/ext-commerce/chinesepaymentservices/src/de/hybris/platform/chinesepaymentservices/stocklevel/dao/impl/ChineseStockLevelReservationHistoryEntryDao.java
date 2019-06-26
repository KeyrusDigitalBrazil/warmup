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
package de.hybris.platform.chinesepaymentservices.stocklevel.dao.impl;

import de.hybris.platform.chinesepaymentservices.model.StockLevelReservationHistoryEntryModel;
import de.hybris.platform.chinesepaymentservices.stocklevel.dao.StockLevelReservationHistoryEntryDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class ChineseStockLevelReservationHistoryEntryDao implements StockLevelReservationHistoryEntryDao
{

	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<StockLevelReservationHistoryEntryModel> getStockLevelReservationHistoryEntryByOrderCode(final String orderCode)
	{
		final String fsq = "SELECT {" + StockLevelReservationHistoryEntryModel.PK + "} FROM {"
				+ StockLevelReservationHistoryEntryModel._TYPECODE + "} WHERE {" + StockLevelReservationHistoryEntryModel.ORDERCODE
				+ "}  = ?orderCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter("orderCode", orderCode);

		return flexibleSearchService.<StockLevelReservationHistoryEntryModel> search(query).getResult();
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
