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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.SaveCartDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default dao implementation for handling the saved cart feature
 */
public class DefaultSaveCartDao extends DefaultCommerceCartDao implements SaveCartDao
{
	protected final static String SAVED_CARTS_CLAUSE = "{" + CartModel.SAVETIME + "} IS NOT NULL";

	protected final static String FIND_SAVED_CARTS_FOR_USER_AND_SITE = SELECTCLAUSE + "WHERE {" + CartModel.USER
			+ "} = ?user AND {" + CartModel.SITE + "} = ?site AND " + SAVED_CARTS_CLAUSE + " ";

	protected final static String FIND_SAVED_CARTS_FOR_USER = SELECTCLAUSE + "WHERE {" + CartModel.USER + "} = ?user AND "
			+ SAVED_CARTS_CLAUSE + " ";

	protected final static String FIND_EXPIRED_SAVED_CARTS_FOR_SITE = SELECTCLAUSE + "WHERE {" + CartModel.SITE + "} = ?site AND "
			+ SAVED_CARTS_CLAUSE + " AND {" + CartModel.EXPIRATIONTIME + "} <= ?currentDate " + ORDERBYCLAUSE;

	protected final static String FIND_SAVED_CARTS_FOR_SITE_AND_USER_WITH_STATUS = "SELECT {" + CartModel.PK + "} FROM {"
			+ CartModel._TYPECODE + "}, {" + OrderStatus._TYPECODE + "} " + "WHERE {" + CartModel._TYPECODE + "." + CartModel.STATUS
			+ "} = {" + OrderStatus._TYPECODE + ".pk} AND {" + CartModel.USER + "} = ?user AND {" + CartModel.SITE + "} = ?site AND "
			+ SAVED_CARTS_CLAUSE + " AND {OrderStatus.CODE} in (?orderStatus) ";

	protected final static String FIND_SAVED_CARTS_FOR_USER_WITH_STATUS = "SELECT {" + CartModel.PK + "} FROM {"
			+ CartModel._TYPECODE + "}, {" + OrderStatus._TYPECODE + "} " + "WHERE {" + CartModel._TYPECODE + "." + CartModel.STATUS
			+ "} = {" + OrderStatus._TYPECODE + ".pk} AND {" + CartModel.USER + "} = ?user AND " + SAVED_CARTS_CLAUSE
			+ " AND {OrderStatus.CODE} in (?orderStatus) ";

	protected final static String SAVED_CARTS_TOTAL_FOR_USER_AND_SITE = "SELECT COUNT({" + CartModel.PK + "}) FROM {"
			+ CartModel._TYPECODE + "} WHERE {" + CartModel.USER + "} = ?user AND {" + CartModel.SITE + "} = ?site AND "
			+ SAVED_CARTS_CLAUSE;

	protected final static String SAVED_CARTS_TOTAL_FOR_USER = "SELECT COUNT({" + CartModel.PK + "}) FROM {" + CartModel._TYPECODE
			+ "} WHERE {" + CartModel.USER + "} = ?user AND " + SAVED_CARTS_CLAUSE;

	protected static final String DATE_MODIFIED_SORT_CRITERIA = "{" + CartModel.MODIFIEDTIME + "} DESC";

	protected static final String SORT_SAVED_CARTS_BY_CODE = " ORDER BY {" + CartModel.CODE + "}, " + DATE_MODIFIED_SORT_CRITERIA;

	protected static final String SORT_SAVED_CARTS_BY_NAME = " ORDER BY {" + CartModel.NAME + "}, " + DATE_MODIFIED_SORT_CRITERIA;

	protected static final String SORT_SAVED_CARTS_BY_DATE_SAVED = " ORDER BY {" + CartModel.SAVETIME + "} DESC";

	protected static final String SORT_SAVED_CARTS_BY_TOTAL = " ORDER BY {" + CartModel.TOTALPRICE + "}, "
			+ DATE_MODIFIED_SORT_CRITERIA;

	protected static final String SORT_CODE_BY_DATE_MODIFIED = "byDateModified";
	protected static final String SORT_CODE_BY_DATE_SAVED = "byDateSaved";
	protected static final String SORT_CODE_BY_NAME = "byName";
	protected static final String SORT_CODE_BY_CODE = "byCode";
	protected static final String SORT_CODE_BY_TOTAL = "byTotal";

	private PagedFlexibleSearchService pagedFlexibleSearchService;

	@Override
	public List<CartModel> getSavedCartsForRemovalForSite(final BaseSiteModel site)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("site", site);
		params.put("currentDate", new Date());


		return doSearch(FIND_EXPIRED_SAVED_CARTS_FOR_SITE, params, CartModel.class);
	}

	@Override
	public Integer getSavedCartsCountForSiteAndUser(final BaseSiteModel baseSite, final UserModel user)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);

		final String query;
		if (baseSite != null)
		{
			params.put("site", baseSite);
			query = SAVED_CARTS_TOTAL_FOR_USER_AND_SITE;
		}
		else
		{
			query = SAVED_CARTS_TOTAL_FOR_USER;
		}

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
		fQuery.addQueryParameters(params);
		fQuery.setResultClassList(Collections.singletonList(Integer.class));

		final SearchResult<Integer> searchResult = search(fQuery);

		return searchResult.getResult().get(0);
	}

	@Override
	public SearchPageData<CartModel> getSavedCartsForSiteAndUser(final PageableData pageableData, final BaseSiteModel baseSite,
			final UserModel user, final List<OrderStatus> orderStatus)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("user", user);

		final String orderStatusList = formatOrderStatusList(orderStatus);
		final String query;
		if (baseSite != null)
		{
			params.put("site", baseSite);
			if (StringUtils.isNotBlank(orderStatusList))
			{
				params.put("orderStatus", orderStatusList);
				query = FIND_SAVED_CARTS_FOR_SITE_AND_USER_WITH_STATUS;
			}
			else
			{
				query = FIND_SAVED_CARTS_FOR_USER_AND_SITE;
			}
		}
		else
		{
			if (StringUtils.isNotBlank(orderStatusList))
			{
				params.put("orderStatus", orderStatusList);
				query = FIND_SAVED_CARTS_FOR_USER_WITH_STATUS;
			}
			else
			{
				query = FIND_SAVED_CARTS_FOR_USER;
			}
		}

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_CODE_BY_DATE_MODIFIED, query + ORDERBYCLAUSE),
				createSortQueryData(SORT_CODE_BY_DATE_SAVED, query + SORT_SAVED_CARTS_BY_DATE_SAVED),
				createSortQueryData(SORT_CODE_BY_NAME, query + SORT_SAVED_CARTS_BY_NAME),
				createSortQueryData(SORT_CODE_BY_CODE, query + SORT_SAVED_CARTS_BY_CODE),
				createSortQueryData(SORT_CODE_BY_TOTAL, query + SORT_SAVED_CARTS_BY_TOTAL));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_CODE_BY_DATE_MODIFIED, params, pageableData);
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	/**
	 * Format given list of OrderStatus to comma-separated string of order status ids in brackets
	 *
	 */
	protected String formatOrderStatusList(final List<OrderStatus> orderStatus)
	{
		if (CollectionUtils.isNotEmpty(orderStatus))
		{
			final StringBuilder orderStatusIdList = new StringBuilder();
			for (final OrderStatus status : orderStatus)
			{
				if (status != null && StringUtils.isNotBlank(status.getCode()))
				{
					orderStatusIdList.append(status.getCode()).append(",");
				}
			}
			if (orderStatusIdList.length() > 0)
			{
				orderStatusIdList.deleteCharAt(orderStatusIdList.length() - 1);
			}
			return orderStatusIdList.toString();
		}
		return null;
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
