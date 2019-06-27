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
package de.hybris.platform.subscriptionservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.customer.dao.impl.DefaultCustomerAccountDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Data Access Object for looking up items related to the Customer's account. It overrides the super class' methods to
 * exclude child orders from the result set.
 */
public class DefaultSubscriptionCustomerAccountDao extends DefaultCustomerAccountDao
{

	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.USER
			+ "} = ?customer AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store AND {"
			+ OrderModel.PARENT + "} IS NULL";

	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS = FIND_ORDERS_BY_CUSTOMER_STORE_QUERY + " AND {"
			+ OrderModel.STATUS + "} IN (?statusList)";

	private static final String SORT_ORDERS_BY_DATE = " ORDER BY {" + OrderModel.CREATIONTIME + "} DESC, {" + OrderModel.PK + "}";

	private static final String SORT_ORDERS_BY_CODE = " ORDER BY {" + OrderModel.CODE + "},{" + OrderModel.CREATIONTIME
			+ "} DESC, {" + OrderModel.PK + "}";


	@Override
	@Nonnull
	public List<OrderModel> findOrdersByCustomerAndStore(@Nonnull final CustomerModel customerModel,
	                                                     @Nonnull final BaseStoreModel store,
														 @Nullable final OrderStatus[] status)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("customer", customerModel);
		queryParams.put("store", store);

		final String query;

		if (status != null && status.length > 0)
		{
			queryParams.put("statusList", Arrays.asList(status));
			query = FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS;
		}
		else
		{
			query = FIND_ORDERS_BY_CUSTOMER_STORE_QUERY;
		}

		final SearchResult<OrderModel> result = getFlexibleSearchService().search(query, queryParams);
		return result.getResult();
	}

	@Override
	@Nonnull
	public SearchPageData<OrderModel> findOrdersByCustomerAndStore(@Nonnull final CustomerModel customerModel,
	                                                               @Nonnull final BaseStoreModel store,
																   @Nullable final OrderStatus[] status,
																   @Nonnull final PageableData pageableData)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");

		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("customer", customerModel);
		queryParams.put("store", store);

		final List<SortQueryData> sortQueries;

		if (status != null && status.length > 0)
		{
			queryParams.put("statusList", Arrays.asList(status));
			sortQueries = Arrays.asList(
					createSortQueryData("byDate", FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS + SORT_ORDERS_BY_DATE),
					createSortQueryData("byOrderNumber", FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS + SORT_ORDERS_BY_CODE));
		}
		else
		{
			sortQueries = Arrays.asList(createSortQueryData("byDate", FIND_ORDERS_BY_CUSTOMER_STORE_QUERY + SORT_ORDERS_BY_DATE),
					createSortQueryData("byOrderNumber", FIND_ORDERS_BY_CUSTOMER_STORE_QUERY + SORT_ORDERS_BY_CODE));
		}

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}

}
