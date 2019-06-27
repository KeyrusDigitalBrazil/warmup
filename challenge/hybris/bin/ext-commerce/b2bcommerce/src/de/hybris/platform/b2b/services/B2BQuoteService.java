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
package de.hybris.platform.b2b.services;

import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;


/**
 * @deprecated Since 6.3. Use quote functionality from commerce instead. ({@link CommerceQuoteService}).<br/>
 *             Service interface for quote related operations.
 */
@Deprecated
public interface B2BQuoteService
{
	/**
	 * Returns a paged list of quotes for the given customer and base store.
	 *
	 * @param customerModel
	 *           the customer to retrieve orders for
	 * @param store
	 *           the current base store
	 * @param pageableData
	 *           pagination information
	 * @return the list of quotes
	 */
	SearchPageData<OrderModel> getQuoteList(CustomerModel customerModel, BaseStoreModel store, PageableData pageableData);
}
