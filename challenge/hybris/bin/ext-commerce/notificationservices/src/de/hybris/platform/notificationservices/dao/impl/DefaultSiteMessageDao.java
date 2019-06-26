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
package de.hybris.platform.notificationservices.dao.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationservices.dao.SiteMessageDao;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link SiteMessageDao}
 */
public class DefaultSiteMessageDao extends DefaultGenericDao<SiteMessageModel> implements SiteMessageDao
{
	private static final String CUSTOMER = "customer";
	private static final String SEARCH_MESSAGE_BY_TYPE = "select {smc:pk} from {" + SiteMessageForCustomerModel._TYPECODE
			+ " as smc left join " + SiteMessageModel._TYPECODE + " as sm on {smc:message} = {sm:pk} left join "
			+ SiteMessageType._TYPECODE
			+ " as smt on {smt:pk}={sm:type}} where {smt:code} = ?type and {smc:customer} = ?customer";

	private static final String SEARCH_MESSAGE = "select {smc:pk} from {" + SiteMessageForCustomerModel._TYPECODE
			+ " as smc left join " + SiteMessageModel._TYPECODE
			+ " as sm on {smc:message} = {sm:pk}} where {smc:customer} = ?customer";

	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;

	private Map<String, String> siteMessageSortCodeToQueryAlias;

	public DefaultSiteMessageDao()
	{
		super(SiteMessageModel._TYPECODE);
	}

	@Override
	public SearchPageData<SiteMessageForCustomerModel> findPaginatedMessagesByType(final CustomerModel customer, final SiteMessageType type,
			final SearchPageData searchPageData)
	{

		Assert.notNull(type, "message type is required");
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		parameter.setSearchPageData(searchPageData);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_MESSAGE_BY_TYPE);
		query.addQueryParameter("type", type.getCode());
		query.addQueryParameter(CUSTOMER, customer);
		parameter.setFlexibleSearchQuery(query);

		parameter.setSortCodeToQueryAlias(siteMessageSortCodeToQueryAlias);

		return getPaginatedFlexibleSearchService().search(parameter);
	}

	@Override
	public SearchPageData<SiteMessageForCustomerModel> findPaginatedMessages(final CustomerModel customer,
			final SearchPageData searchPageData)
	{
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		parameter.setSearchPageData(searchPageData);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_MESSAGE);
		query.addQueryParameter(CUSTOMER, customer);
		parameter.setFlexibleSearchQuery(query);

		parameter.setSortCodeToQueryAlias(siteMessageSortCodeToQueryAlias);

		return getPaginatedFlexibleSearchService().search(parameter);
	}

	@Override
	public List<SiteMessageForCustomerModel> findSiteMessagesForCustomer(final CustomerModel customer)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_MESSAGE);
		query.addQueryParameter(CUSTOMER, customer);
		final List<SiteMessageForCustomerModel> results = getFlexibleSearchService().<SiteMessageForCustomerModel> search(query)
				.getResult();
		if (CollectionUtils.isNotEmpty(results))
		{
			return results;
		}
		return Collections.emptyList();

	}

	protected PaginatedFlexibleSearchService getPaginatedFlexibleSearchService()
	{
		return paginatedFlexibleSearchService;
	}

	@Required
	public void setPaginatedFlexibleSearchService(final PaginatedFlexibleSearchService paginatedFlexibleSearchService)
	{
		this.paginatedFlexibleSearchService = paginatedFlexibleSearchService;
	}

	protected Map<String, String> getSiteMessageSortCodeToQueryAlias()
	{
		return siteMessageSortCodeToQueryAlias;
	}

	@Required
	public void setSiteMessageSortCodeToQueryAlias(final Map<String, String> customerCouponSortCodeToQueryAlias)
	{
		this.siteMessageSortCodeToQueryAlias = customerCouponSortCodeToQueryAlias;
	}

}
