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
package de.hybris.platform.b2bcommercefacades.order;

import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * @deprecated Since 6.3. Use quote functionality from commerce instead ({@link QuoteFacade}).<br/>
 *             Facade interface for quote operations.
 */
@Deprecated
public interface B2BQuoteFacade
{
	/**
	 * Returns the quote history of the current user.
	 *
	 * @param pageableData
	 *           paging information
	 * @return The quote history of the current user.
	 */
	SearchPageData<OrderHistoryData> getQuoteHistory(final PageableData pageableData);
}
