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
package de.hybris.platform.commerceservices.customer.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.customer.dao.CustomerAccountDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCustomerAccountDao extends AbstractItemDao implements CustomerAccountDao
{
	// Order Queries
	private static final String FIND_ORDERS_BY_CUSTOMER_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE
			+ "} = ?code AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.USER + "} = ?customer AND {"
			+ OrderModel.STORE + "} = ?store";

	private static final String FIND_ORDERS_BY_CODE_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE + "} = ?code AND {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store";

	private static final String FIND_ORDERS_BY_GUID_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {" + OrderModel.CREATIONTIME
			+ "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.GUID + "} = ?guid AND  {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store";

	private static final String EXPIRY_DATE_OPTION = " AND {" + OrderModel.MODIFIEDTIME + "} >= ?expiryDate";

	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY = "SELECT {" + OrderModel.PK + "}, {"
			+ OrderModel.CREATIONTIME + "}, {" + OrderModel.CODE + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.USER
			+ "} = ?customer AND {" + OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store";

	private static final String FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS = FIND_ORDERS_BY_CUSTOMER_STORE_QUERY + " AND {"
			+ OrderModel.STATUS + "} IN (?statusList)";

	private static final String FILTER_ORDER_STATUS = " AND {" + OrderModel.STATUS + "} NOT IN (?filterStatusList)";

	private static final String SORT_ORDERS_BY_DATE = " ORDER BY {" + OrderModel.CREATIONTIME + "} DESC, {" + OrderModel.PK + "}";

	private static final String SORT_ORDERS_BY_CODE = " ORDER BY {" + OrderModel.CODE + "},{" + OrderModel.CREATIONTIME
			+ "} DESC, {" + OrderModel.PK + "}";

	//Return Request queries
	protected static final String FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY =
			"SELECT {return:" + ReturnRequestModel.PK + "}, {return:" + ReturnRequestModel.CREATIONTIME + "}" + " FROM {"
					+ ReturnRequestModel._TYPECODE + " AS return" + " JOIN " + OrderModel._TYPECODE + " AS order ON {return:"
					+ ReturnRequestModel.ORDER + "} = {order:" + OrderModel.PK + "}}" + " WHERE {order:" + OrderModel.USER
					+ "} = ?customer AND {order:" + OrderModel.VERSIONID + "} IS NULL AND {order:" + OrderModel.STORE + "} = ?store";

	protected static final String FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY_AND_STATUS =
			FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY + " AND {return:" + ReturnRequestModel.STATUS + "} IN (?statusList)";

	protected static final String FILTER_RETURNREQUESTS_STATUS =
			" AND {return:" + ReturnRequestModel.STATUS + "} NOT IN (?filterStatusList)";

	protected static final String SORT_RETURNREQUESTS_BY_DATE =
			" ORDER BY {return:" + ReturnRequestModel.CREATIONTIME + "} DESC, {return:" + ReturnRequestModel.PK + "}";

	protected static final String SORT_RETURNREQUESTS_BY_RMA =
			" ORDER BY {return:" + ReturnRequestModel.RMA + "},{return:" + ReturnRequestModel.CREATIONTIME + "} DESC, {return:"
					+ ReturnRequestModel.PK + "}";

	// CreditCardPaymentInfo Queries

	private static final String FIND_SAVED_PAYMENT_INFOS_BY_CUSTOMER_QUERY = "SELECT {" + CreditCardPaymentInfoModel.PK
			+ "} FROM {" + CreditCardPaymentInfoModel._TYPECODE + "} WHERE {" + CreditCardPaymentInfoModel.USER
			+ "} = ?customer AND {" + CreditCardPaymentInfoModel.SAVED + "} = ?saved AND {" + CreditCardPaymentInfoModel.DUPLICATE
			+ "} = ?duplicate";

	private static final String FIND_PAYMENT_INFOS_BY_CUSTOMER_QUERY = "SELECT {" + CreditCardPaymentInfoModel.PK + "} FROM {"
			+ CreditCardPaymentInfoModel._TYPECODE + "} WHERE {" + CreditCardPaymentInfoModel.USER + "} = ?customer AND {"
			+ CreditCardPaymentInfoModel.DUPLICATE + "} = ?duplicate";

	private static final String FIND_PAYMENT_INFO_BY_CUSTOMER_QUERY = "SELECT {" + CreditCardPaymentInfoModel.PK + "} FROM {"
			+ CreditCardPaymentInfoModel._TYPECODE + "} WHERE {" + CreditCardPaymentInfoModel.USER + "} = ?customer AND {"
			+ CreditCardPaymentInfoModel.PK + "} = ?pk AND {" + CreditCardPaymentInfoModel.DUPLICATE + "} = ?duplicate";

	// Address Queries
	private static final String FIND_ADDRESS_BOOK_DELIVERY_ENTRIES = "SELECT {address:" + AddressModel.PK + "} FROM {"
			+ AddressModel._TYPECODE + " AS address LEFT JOIN " + CustomerModel._TYPECODE + " AS customer ON {address:"
			+ AddressModel.OWNER + "}={customer:" + CustomerModel.PK + "}} WHERE {customer:" + CustomerModel.PK
			+ "} = ?customer AND {address:" + AddressModel.SHIPPINGADDRESS + "} = ?shippingAddress AND {address:"
			+ AddressModel.VISIBLEINADDRESSBOOK + "} = ?visibleInAddressBook AND {address:" + AddressModel.COUNTRY
			+ "} IN (?deliveryCountries)";

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	private List<OrderStatus> filterOrderStatusList;
	private List<ReturnStatus> filterReturnStatusList;

	@Override
	public OrderModel findOrderByCustomerAndCodeAndStore(final CustomerModel customerModel, final String code,
			final BaseStoreModel store)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(code, "Code must not be null");
		validateParameterNotNull(store, "Store must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("code", code);
		queryParams.put("store", store);
		final OrderModel result = getFlexibleSearchService().searchUnique(
				new FlexibleSearchQuery(FIND_ORDERS_BY_CUSTOMER_CODE_STORE_QUERY, queryParams));
		return result;
	}

	@Override
	public OrderModel findOrderByCodeAndStore(final String code, final BaseStoreModel store)
	{
		validateParameterNotNull(code, "Code must not be null");
		validateParameterNotNull(store, "Store must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("code", code);
		queryParams.put("store", store);
		final OrderModel result = getFlexibleSearchService().searchUnique(
				new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_STORE_QUERY, queryParams));
		return result;
	}


	@Override
	public OrderModel findOrderByGUIDAndStore(final String guid, final BaseStoreModel store, final Date expiryDate)
	{
		validateParameterNotNull(guid, "GUID must not be null");
		validateParameterNotNull(store, "Store must not be null");
		validateParameterNotNull(store, "Expiry Date must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("guid", guid);
		if (expiryDate != null)
		{
			queryParams.put("expiryDate", expiryDate);
		}
		queryParams.put("store", store);
		final OrderModel result = getFlexibleSearchService()
				.searchUnique(
						new FlexibleSearchQuery(FIND_ORDERS_BY_GUID_STORE_QUERY + (expiryDate != null ? EXPIRY_DATE_OPTION : ""),
								queryParams));
		return result;
	}

	@Override
	public List<OrderModel> findOrdersByCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderStatus[] status)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("store", store);

		String filterClause = StringUtils.EMPTY;
		if (CollectionUtils.isNotEmpty(getFilterOrderStatusList()))
		{
			queryParams.put("filterStatusList", getFilterOrderStatusList());
			filterClause = FILTER_ORDER_STATUS;
		}

		String query;
		if (ArrayUtils.isNotEmpty(status))
		{
			queryParams.put("statusList", Arrays.asList(status));
			query = createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS, filterClause);
		}
		else
		{
			query = createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY, filterClause);
		}

		final SearchResult<OrderModel> result = getFlexibleSearchService().search(query, queryParams);
		return result.getResult();
	}

	@Override
	public SearchPageData<OrderModel> findOrdersByCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final OrderStatus[] status, final PageableData pageableData)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("store", store);

		String filterClause = StringUtils.EMPTY;
		if (CollectionUtils.isNotEmpty(getFilterOrderStatusList()))
		{
			queryParams.put("filterStatusList", getFilterOrderStatusList());
			filterClause = FILTER_ORDER_STATUS;
		}

		final List<SortQueryData> sortQueries;

		if (ArrayUtils.isNotEmpty(status))
		{
			queryParams.put("statusList", Arrays.asList(status));
			sortQueries = Arrays.asList(
					createSortQueryData("byDate",
							createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS, filterClause, SORT_ORDERS_BY_DATE)),
					createSortQueryData("byOrderNumber",
							createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY_AND_STATUS, filterClause, SORT_ORDERS_BY_CODE)));
		}
		else
		{
			sortQueries = Arrays
					.asList(
							createSortQueryData("byDate",
									createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY, filterClause, SORT_ORDERS_BY_DATE)),
					createSortQueryData("byOrderNumber",
							createQuery(FIND_ORDERS_BY_CUSTOMER_STORE_QUERY, filterClause, SORT_ORDERS_BY_CODE)));
		}

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}

	@Override
	public SearchPageData<ReturnRequestModel> findReturnRequestsByCustomerAndStore(final CustomerModel customerModel,
			final BaseStoreModel store, final ReturnStatus[] returnStatuses, final PageableData pageableData)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");
		validateParameterNotNull(pageableData, "PageableData must not be null");

		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("store", store);

		String filterClause = StringUtils.EMPTY;
		if (CollectionUtils.isNotEmpty(getFilterReturnStatusList()))
		{
			queryParams.put("filterStatusList", getFilterReturnStatusList());
			filterClause = FILTER_RETURNREQUESTS_STATUS;
		}

		final List<SortQueryData> sortQueries;
		if (ArrayUtils.isNotEmpty(returnStatuses))
		{
			queryParams.put("statusList", Arrays.asList(returnStatuses));
			sortQueries = Arrays.asList(createSortQueryData("byDate",
					createQuery(FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY_AND_STATUS, filterClause, SORT_RETURNREQUESTS_BY_DATE)),
					createSortQueryData("byRMA", createQuery(FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY_AND_STATUS, filterClause,
							SORT_RETURNREQUESTS_BY_RMA)));
		}
		else
		{
			sortQueries = Arrays.asList(createSortQueryData("byDate",
							createQuery(FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY, filterClause, SORT_RETURNREQUESTS_BY_DATE)),
					createSortQueryData("byRMA",
							createQuery(FIND_RETURNREQUESTS_BY_CUSTOMER_STORE_QUERY, filterClause, SORT_RETURNREQUESTS_BY_RMA)));
		}

		return getPagedFlexibleSearchService().search(sortQueries, "byDate", queryParams, pageableData);
	}

	@Override
	public List<CreditCardPaymentInfoModel> findCreditCardPaymentInfosByCustomer(final CustomerModel customerModel,
			final boolean saved)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		if (saved)
		{
			queryParams.put("saved", Boolean.TRUE);
		}
		queryParams.put("duplicate", Boolean.FALSE);
		final SearchResult<CreditCardPaymentInfoModel> result = getFlexibleSearchService().search(
				saved ? FIND_SAVED_PAYMENT_INFOS_BY_CUSTOMER_QUERY : FIND_PAYMENT_INFOS_BY_CUSTOMER_QUERY, queryParams);
		return result.getResult();
	}

	@Override
	public CreditCardPaymentInfoModel findCreditCardPaymentInfoByCustomer(final CustomerModel customerModel, final String code)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("duplicate", Boolean.FALSE);
		queryParams.put("pk", PK.parse(code));
		final SearchResult<CreditCardPaymentInfoModel> result = getFlexibleSearchService().search(
				FIND_PAYMENT_INFO_BY_CUSTOMER_QUERY, queryParams);
		return result.getCount() > 0 ? result.getResult().get(0) : null;
	}

	@Override
	public List<AddressModel> findAddressBookDeliveryEntriesForCustomer(final CustomerModel customerModel,
			final Collection<CountryModel> deliveryCountries)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("customer", customerModel);
		queryParams.put("shippingAddress", Boolean.TRUE);
		queryParams.put("visibleInAddressBook", Boolean.TRUE);
		queryParams.put("deliveryCountries", deliveryCountries);
		final SearchResult<AddressModel> result = getFlexibleSearchService().search(FIND_ADDRESS_BOOK_DELIVERY_ENTRIES,
				queryParams);
		return result.getResult();
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	protected String createQuery(final String... queryClauses)
	{
		final StringBuilder queryBuilder = new StringBuilder();

		for (final String queryClause : queryClauses)
		{
			queryBuilder.append(queryClause);
		}

		return queryBuilder.toString();
	}

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

	protected List<OrderStatus> getFilterOrderStatusList()
	{
		return filterOrderStatusList;
	}

	/**
	 * Optional list of {@link OrderStatus} values to be filtered.
	 */
	public void setFilterOrderStatusList(final List<OrderStatus> filterOrderStatusList)
	{
		this.filterOrderStatusList = filterOrderStatusList;
	}

	protected List<ReturnStatus> getFilterReturnStatusList()
	{
		return filterReturnStatusList;
	}

	/**
	 * Optional list of {@link ReturnStatus} values to be filtered.
	 */
	public void setFilterReturnStatusList(final List<ReturnStatus> filterReturnStatusList)
	{
		this.filterReturnStatusList = filterReturnStatusList;
	}
}
