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
package de.hybris.platform.commerceservices.order.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.dao.CommerceOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link CommerceOrderDao} interface extending {@link DefaultGenericDao}
 */
public class DefaultCommerceOrderDao extends DefaultGenericDao<OrderModel> implements CommerceOrderDao
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceOrderDao.class);

	public DefaultCommerceOrderDao()
	{
		super(OrderModel._TYPECODE);
	}

	private static final String FIND_ORDERS_BY_QUOTE_QUERY = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE
			+ "} WHERE {" + OrderModel.QUOTEREFERENCE + "} = ?quoteReference AND {" + OrderModel.VERSIONID + "} IS NULL";

	@Override
	public OrderModel findOrderByQuote(final QuoteModel quote)
	{
		validateParameterNotNullStandardMessage("QuoteModel", quote);
		OrderModel orderModel = null;
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("quoteReference", quote);
		final SearchResult<OrderModel> resultSearchList = getFlexibleSearchService()
				.search(new FlexibleSearchQuery(FIND_ORDERS_BY_QUOTE_QUERY, queryParams));
		final List<OrderModel> resultList = resultSearchList.getResult();
		final int size = CollectionUtils.size(resultList);
		if (size == 1)
		{
			orderModel = resultList.get(0);
		}
		else if (size > 1)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("More than 1 order found for Quote pk:%s code:%s, version:%s", quote.getPk(), quote.getCode(),
						quote.getVersion()));
			}
		}
		return orderModel;
	}

}
