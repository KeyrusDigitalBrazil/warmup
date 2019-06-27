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
package de.hybris.platform.b2bcommercefacades.order.impl;

import de.hybris.platform.b2b.services.B2BQuoteService;
import de.hybris.platform.b2bcommercefacades.order.B2BQuoteFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.order.impl.DefaultOrderFacade;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated Since 6.3. Use quote functionality from commerce instead ({@link QuoteFacade}).<br/>
 *             Default implementation of the {@link B2BQuoteFacade} interface.
 */
@Deprecated
public class DefaultB2BQuoteFacade extends DefaultOrderFacade implements B2BQuoteFacade
{
	private B2BQuoteService quoteService;

	@Override
	public SearchPageData<OrderHistoryData> getQuoteHistory(final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<OrderModel> quoteResults = getQuoteService().getQuoteList(currentCustomer, currentBaseStore,
				pageableData);

		return convertPageData(quoteResults, getOrderHistoryConverter());
	}

	protected B2BQuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final B2BQuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

}
