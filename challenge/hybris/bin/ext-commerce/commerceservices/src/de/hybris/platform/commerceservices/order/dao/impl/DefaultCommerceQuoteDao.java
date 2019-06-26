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

import static de.hybris.platform.commerceservices.constants.GeneratedCommerceServicesConstants.Relations.QUOTETONOTIFICATIONREL;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.enums.QuoteNotificationType;
import de.hybris.platform.commerceservices.order.dao.CommerceQuoteDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CommerceQuoteDao} interface extending {@link DefaultGenericDao}
 */
public class DefaultCommerceQuoteDao extends DefaultGenericDao<QuoteModel> implements CommerceQuoteDao
{
	private static final String FIND_QUOTES_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY = "SELECT {q1:" + QuoteModel.PK + "} FROM {"
			+ QuoteModel._TYPECODE + " as q1} WHERE {q1:" + QuoteModel.STATE + "} IN (?quoteStates) AND {q1:" + QuoteModel.USER
			+ "} = ?" + QuoteModel.USER + " AND {q1:" + QuoteModel.STORE + "} = ?" + QuoteModel.STORE + " AND {q1:"
			+ QuoteModel.VERSION + "} = ({{ SELECT MAX({" + QuoteModel.VERSION + "}) FROM {" + QuoteModel._TYPECODE + "} WHERE {"
			+ QuoteModel.CODE + "} = {q1:" + QuoteModel.CODE + "} AND {" + QuoteModel.STATE + "} IN (?quoteStates) AND {"
			+ QuoteModel.USER + "} = ?" + QuoteModel.USER + " AND {" + QuoteModel.STORE + "} = ?" + QuoteModel.STORE + "}}) ";

	private static final String FIND_QUOTES_SOON_TO_EXPIRE = "SELECT {q1:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
			+ " as q1} WHERE {q1:" + QuoteModel.STATE + "} IN (?quoteStates) AND {q1:" + QuoteModel.EXPIRATIONTIME
			+ "} > ?expiredAfter AND {q1:" + QuoteModel.EXPIRATIONTIME + "} <= ?expiredBy AND {q1:" + QuoteModel.VERSION
			+ "} = ({{ SELECT MAX({" + QuoteModel.VERSION + "}) FROM {" + QuoteModel._TYPECODE + "} WHERE {" + QuoteModel.CODE
			+ "} = {q1:" + QuoteModel.CODE + "} }}) AND NOT EXISTS ({{ SELECT {rel:pk} FROM {" + QUOTETONOTIFICATIONREL
			+ " AS rel} WHERE {rel:source} = {q1:" + QuoteModel.PK + "} AND {rel:target} = ?notificationType }}) ";

	private static final String FIND_QUOTES_BY_EXPIRATION_TIME = "SELECT {q1:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
			+ " as q1} WHERE {q1:" + QuoteModel.STATE + "} IN (?quoteStates) AND {q1:" + QuoteModel.EXPIRATIONTIME
			+ "} <= ?currentDate AND {q1:" + QuoteModel.VERSION + "} = ({{ SELECT MAX({" + QuoteModel.VERSION + "}) FROM {"
			+ QuoteModel._TYPECODE + "} WHERE {" + QuoteModel.CODE + "} = {q1:" + QuoteModel.CODE
			+ "} }}) AND NOT EXISTS ({{ SELECT {rel:pk} FROM {" + QUOTETONOTIFICATIONREL + " AS rel} WHERE {rel:source} = {q1:"
			+ QuoteModel.PK + "} AND {rel:target} = ?notificationType }}) ";

	private static final String FIND_QUOTE_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY = "SELECT {q1:" + QuoteModel.PK + "} FROM {"
			+ QuoteModel._TYPECODE + " as q1} WHERE {q1:" + QuoteModel.STATE + "} IN (?quoteStates) AND {q1:" + QuoteModel.USER
			+ "} = ?" + QuoteModel.USER + " AND {q1:" + QuoteModel.STORE + "} = ?" + QuoteModel.STORE + " AND {q1:" + QuoteModel.CODE
			+ "} = ?" + QuoteModel.CODE + "  ORDER BY {q1:" + QuoteModel.VERSION + "} DESC";

	private static final String QUOTES_TOTAL_FOR_USER_AND_STORE = "SELECT COUNT(DISTINCT {q1:" + QuoteModel.CODE + "}) FROM {"
			+ QuoteModel._TYPECODE + " as q1} WHERE {q1:" + QuoteModel.USER + "} = ?" + QuoteModel.USER + " AND {q1:"
			+ QuoteModel.STORE + "} = ?" + QuoteModel.STORE + " AND {q1:" + QuoteModel.STATE + "} IN (?quoteStates)";

	private static final String ORDER_BY_QUOTE_CODE_DESC = " ORDER BY {q1:" + QuoteModel.CODE + "} DESC";
	private static final String ORDER_BY_QUOTE_NAME_DESC = " ORDER BY {q1:" + QuoteModel.NAME + "} DESC";
	private static final String ORDER_BY_QUOTE_DATE_DESC = " ORDER BY {q1:" + QuoteModel.MODIFIEDTIME + "} DESC";
	private static final String ORDER_BY_QUOTE_STATE = " ORDER BY {q1:" + QuoteModel.STATE + "} DESC";

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	public DefaultCommerceQuoteDao()
	{
		super(QuoteModel._TYPECODE);
	}

	@Override
	public SearchPageData<QuoteModel> findQuotesByCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final PageableData pageableData, final Set<QuoteState> quoteStates)
	{
		validateUserAndStoreAndStates(store, customerModel, quoteStates);

		final Map<String, Object> queryParams = populateBasicQueryParams(store, customerModel, quoteStates);
		final List<SortQueryData> sortQueries;
		sortQueries = Arrays.asList(
				createSortQueryData("byDate",
						createQuery(FIND_QUOTES_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY, ORDER_BY_QUOTE_DATE_DESC)),
				createSortQueryData("byCode",
						createQuery(FIND_QUOTES_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY, ORDER_BY_QUOTE_CODE_DESC)),
				createSortQueryData("byName",
						createQuery(FIND_QUOTES_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY, ORDER_BY_QUOTE_NAME_DESC)),
				createSortQueryData("byState",
						createQuery(FIND_QUOTES_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY, ORDER_BY_QUOTE_STATE)));

		return getPagedFlexibleSearchService().search(sortQueries, "byCode", queryParams, pageableData);
	}

	@Override
	public SearchResult<QuoteModel> findQuotesSoonToExpire(final Date expiredAfter, final Date expiredBy,
			final QuoteNotificationType notificationType, final Set<QuoteState> quoteStates)
	{
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("expiredAfter", expiredAfter);
		queryParams.put("expiredBy", expiredBy);
		queryParams.put("notificationType", notificationType);
		queryParams.put("quoteStates", quoteStates);

		return getFlexibleSearchService().search(FIND_QUOTES_SOON_TO_EXPIRE, queryParams);
	}

	@Override
	public SearchResult<QuoteModel> findQuotesExpired(final Date currentDate, final QuoteNotificationType notificationType,
			final Set<QuoteState> quoteStates)
	{
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("currentDate", currentDate);
		queryParams.put("notificationType", notificationType);
		queryParams.put("quoteStates", quoteStates);

		return getFlexibleSearchService().search(FIND_QUOTES_BY_EXPIRATION_TIME, queryParams);
	}

	@Override
	public QuoteModel findUniqueQuoteByCodeAndCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final String quoteCode, final Set<QuoteState> quoteStates)
	{
		validateParameterNotNull(quoteCode, "Quote Code cannot be null");
		validateUserAndStoreAndStates(store, customerModel, quoteStates);

		final Map<String, Object> queryParams = populateBasicQueryParams(store, customerModel, quoteStates);
		queryParams.put(QuoteModel.CODE, quoteCode);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_QUOTE_BY_CUSTOMER_STORE_CODE_MAX_VERSION_QUERY);
		flexibleSearchQuery.getQueryParameters().putAll(queryParams);
		flexibleSearchQuery.setCount(1);

		return getFlexibleSearchService().searchUnique(flexibleSearchQuery);
	}

	@Override
	public Integer getQuotesCountForCustomerAndStore(final CustomerModel customerModel, final BaseStoreModel store,
			final Set<QuoteState> quoteStates)
	{
		validateUserAndStoreAndStates(store, customerModel, quoteStates);

		final Map<String, Object> queryParams = populateBasicQueryParams(store, customerModel, quoteStates);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(QUOTES_TOTAL_FOR_USER_AND_STORE);
		flexibleSearchQuery.addQueryParameters(queryParams);
		flexibleSearchQuery.setResultClassList(Collections.singletonList(Integer.class));

		final SearchResult<Integer> searchResult = getFlexibleSearchService().search(flexibleSearchQuery);

		return searchResult.getResult().get(0);
	}

	protected Map<String, Object> populateBasicQueryParams(final BaseStoreModel store, final CustomerModel customerModel,
			final Set<QuoteState> quoteStates)
	{
		final Map<String, Object> queryParams = new HashMap<>();
		queryParams.put(QuoteModel.USER, customerModel);
		queryParams.put(QuoteModel.STORE, store);
		queryParams.put("quoteStates", quoteStates);
		return queryParams;
	}

	protected void validateUserAndStoreAndStates(final BaseStoreModel store, final CustomerModel customerModel,
			final Set<QuoteState> quoteStates)
	{
		validateParameterNotNull(customerModel, "Customer must not be null");
		validateParameterNotNull(store, "Store must not be null");
		validateQuoteStateList(quoteStates, "Quote states cannot be null or empty");
	}

	protected void validateQuoteStateList(final Set<QuoteState> quoteStates, final String msg)
	{
		if (CollectionUtils.isEmpty(quoteStates))
		{
			throw new IllegalArgumentException(msg);
		}
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
}
