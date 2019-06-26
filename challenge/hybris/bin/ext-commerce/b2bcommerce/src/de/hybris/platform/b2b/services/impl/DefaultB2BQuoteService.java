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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.services.B2BQuoteService;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated Since 6.3. Use quote functionality from commerce instead. ({@link CommerceQuoteService}).<br/>
 *             Default implementation of the {@link B2BQuoteService} interface.
 */
@Deprecated
public class DefaultB2BQuoteService implements B2BQuoteService
{
	private CustomerAccountDao customerAccountDao;

	private OrderStatus[] quoteOrderStatusArray;

	@Override
	public SearchPageData<OrderModel> getQuoteList(final CustomerModel customerModel, final BaseStoreModel store,
			final PageableData pageableData)
	{
		return getCustomerAccountDao().findOrdersByCustomerAndStore(customerModel, store, getQuoteOrderStatusArray(), pageableData);
	}

	protected CustomerAccountDao getCustomerAccountDao()
	{
		return customerAccountDao;
	}

	@Required
	public void setCustomerAccountDao(final CustomerAccountDao customerAccountDao)
	{
		this.customerAccountDao = customerAccountDao;
	}

	protected OrderStatus[] getQuoteOrderStatusArray()
	{
		return quoteOrderStatusArray;
	}

	@Required
	public void setQuoteOrderStatusArray(final OrderStatus[] quoteOrderStatusList)
	{
		this.quoteOrderStatusArray = quoteOrderStatusList;
	}

}
