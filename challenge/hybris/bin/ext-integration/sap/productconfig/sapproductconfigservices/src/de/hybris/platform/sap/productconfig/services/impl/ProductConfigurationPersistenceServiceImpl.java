/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;
import de.hybris.platform.servicelayer.search.paginated.util.PaginatedSearchUtils;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation for the {@link ProductConfigurationPersistenceService}.
 */
public class ProductConfigurationPersistenceServiceImpl implements ProductConfigurationPersistenceService
{

	private static final String ARGUMENT_CONFIG_ID_CANNOT_BE_NULL = "Argument configId cannot be null";
	private static final String PARAM_NAME_CONFIG_ID = "configId";
	private FlexibleSearchService flexibleSearchService;
	private UserService userService;
	private SessionService sessionService;
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;

	static final String SELECT_BY_CONFIG_ID = "SELECT {pk} FROM { ProductConfiguration } WHERE {configurationId} = ?configId";
	static final String SELECT_BY_PRODUCT_CODE_AND_USER = "SELECT {pc:pk} FROM {ProductConfiguration as pc JOIN Product2ProductConfigs as p2pc on {pc:pk} = {p2pc:target} JOIN Product as p on {p:pk} = {p2pc:source} } where {p:code} = ?productCode AND {pc:user} = ?userPk";
	static final String SELECT_BY_PRODUCT_CODE_AND_SESSIONID = "SELECT {pc:pk} FROM {ProductConfiguration as pc JOIN Product2ProductConfigs as p2pc on {pc:pk} = {p2pc:target} JOIN Product as p on {p:pk} = {p2pc:source} } where {p:code} = ?productCode AND {pc:userSessionId} = ?sessionId";
	static final String SELECT_BY_USER_SESSION_ID = "SELECT {pk} FROM {ProductConfiguration} WHERE {userSessionId} = ?userSessionId";
	static final String SELECT_ENTRY_BY_PK = "SELECT {pk} FROM { AbstractOrderEntry } WHERE {pk} = ?cartEntryKey";
	static final String SELECT_ENTRY_BY_CONFIG_ID_TEMPLATE = "SELECT {aoe:pk} FROM { AbstractOrderEntry AS aoe JOIN ProductConfiguration AS pc ON  {aoe:%s} = {pc:pk}} WHERE {pc:configurationId}= ?configId";
	static final String SELECT_ENTRY_BY_CONFIG_ID = String.format(SELECT_ENTRY_BY_CONFIG_ID_TEMPLATE, "productConfiguration");
	static final String SELECT_ENTRY_BY_DRAFT_CONFIG_ID = String.format(SELECT_ENTRY_BY_CONFIG_ID_TEMPLATE,
			"productConfigurationDraft");
	static final String SELECT_PRODUCT_RELATED = "SELECT {pc.pk} FROM {ProductConfiguration as pc JOIN Product2ProductConfigs  as p2pc on {pc:pk} = {p2pc:target }} WHERE {pc.modifiedTime} <= ?modifiedTime";
	static final String SELECT_ORPHANED = "select {pc.pk} from {ProductConfiguration as pc left outer JOIN Product2ProductConfigs as p2pc on {pc:pk} = {p2pc:target}} where {p2pc:target} is null and NOT EXISTS ({{SELECT {pk} from {AbstractOrderEntry} WHERE {productconfiguration} = {pc.PK}}})";
	static final String SELECT_ALL = "select {pk} from {ProductConfiguration}";
	static final String SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY_TEMPLATE = "SELECT {aoe:pk} FROM { AbstractOrderEntry AS aoe JOIN ProductConfiguration AS pc ON  {aoe:%s} = {pc:pk}} WHERE {pc:configurationId} = ?configId and {aoe.pk} != ?cartEntryKey";
	static final String SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY = String.format(SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY_TEMPLATE,
			"productConfiguration");
	static final String SELECT_BY_DRAFT_CONFIG_ID_AND_CART_ENTRY_KEY = String
			.format(SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY_TEMPLATE, "productConfigurationDraft");


	@Override
	public ProductConfigurationModel getByConfigId(final String configId, final boolean allowNull)
	{
		validateParameterNotNull(configId, ARGUMENT_CONFIG_ID_CANNOT_BE_NULL);
		final Map<String, String> params = Collections.singletonMap(PARAM_NAME_CONFIG_ID, configId);

		final SearchResult search = getFlexibleSearchService().search(new FlexibleSearchQuery(SELECT_BY_CONFIG_ID, params));

		if (0 == search.getCount())
		{
			if (allowNull)
			{
				return null;
			}
			else
			{
				throw new ConfigurationNotFoundException(String.format("ProductConfigurationModel with id='%s' not found", configId));
			}
		}
		if (search.getCount() > 1)
		{
			throw new AmbiguousIdentifierException("More than one config was found with configId=" + configId);
		}
		return (ProductConfigurationModel) search.getResult().get(0);

	}

	@Override
	public AbstractOrderEntryModel getOrderEntryByConfigId(final String configId, final boolean isDraft)
	{
		validateParameterNotNull(configId, ARGUMENT_CONFIG_ID_CANNOT_BE_NULL);
		final Map<String, String> params = Collections.singletonMap(PARAM_NAME_CONFIG_ID, configId);
		String query = SELECT_ENTRY_BY_CONFIG_ID;
		if (isDraft)
		{
			query = SELECT_ENTRY_BY_DRAFT_CONFIG_ID;
		}
		final SearchResult search = getFlexibleSearchService().search(new FlexibleSearchQuery(query, params));

		if (0 == search.getCount())
		{
			return null;
		}
		if (search.getCount() > 1)
		{
			throw new AmbiguousIdentifierException("More than one config was found with configId=" + configId);
		}
		return (AbstractOrderEntryModel) search.getResult().get(0);
	}

	@Override
	public List<AbstractOrderEntryModel> getAllOrderEntriesByConfigId(final String configId)
	{
		validateParameterNotNull(configId, ARGUMENT_CONFIG_ID_CANNOT_BE_NULL);
		final Map<String, String> params = Collections.singletonMap(PARAM_NAME_CONFIG_ID, configId);
		final SearchResult search1 = getFlexibleSearchService().search(new FlexibleSearchQuery(SELECT_ENTRY_BY_CONFIG_ID, params));
		final SearchResult search2 = getFlexibleSearchService()
				.search(new FlexibleSearchQuery(SELECT_ENTRY_BY_DRAFT_CONFIG_ID, params));
		final List<AbstractOrderEntryModel> result = new ArrayList(search1.getCount() + search2.getCount());
		result.addAll(search1.getResult());
		result.addAll(search2.getResult());
		return result;
	}

	@Override
	public ProductConfigurationModel getByProductCode(final String productCode)
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		return getByProductCodeAndUser(productCode, currentUser);
	}

	@Override
	public ProductConfigurationModel getByProductCodeAndUser(final String productCode, final UserModel user)
	{
		validateParameterNotNull(productCode, "Argument productCode cannot be null");
		validateParameterNotNull(user, "Argument user cannot be null");
		final Map<String, String> params = new HashMap();
		params.put("productCode", productCode);
		final String queryTemplate;
		if (userService.isAnonymousUser(user))
		{
			params.put("sessionId", getSessionService().getCurrentSession().getSessionId());
			queryTemplate = SELECT_BY_PRODUCT_CODE_AND_SESSIONID;
		}
		else
		{
			params.put("userPk", user.getPk().toString());
			queryTemplate = SELECT_BY_PRODUCT_CODE_AND_USER;
		}
		final SearchResult result = getFlexibleSearchService().search(new FlexibleSearchQuery(queryTemplate, params));
		if (0 == result.getCount())
		{
			return null;
		}
		if (result.getCount() > 1)
		{
			throw new AmbiguousIdentifierException(
					"More than one config was found for productCode=" + productCode + " and current user");
		}
		return (ProductConfigurationModel) result.getResult().get(0);
	}


	@Override
	public AbstractOrderEntryModel getOrderEntryByPK(final String cartEntryKey)
	{
		validateParameterNotNull(cartEntryKey, "Argument cartEntryKey cannot be null");
		final Map<String, String> params = Collections.singletonMap("cartEntryKey", cartEntryKey);
		return getFlexibleSearchService().searchUnique(new FlexibleSearchQuery(SELECT_ENTRY_BY_PK, params));
	}

	@Override
	public List<ProductConfigurationModel> getByUserSessionId(final String userSessionId)
	{
		validateParameterNotNull(userSessionId, "Argument userSessionId cannot be null");
		final Map<String, String> params = Collections.singletonMap("userSessionId", userSessionId);
		final SearchResult<ProductConfigurationModel> searchResult = getFlexibleSearchService()
				.search(new FlexibleSearchQuery(SELECT_BY_USER_SESSION_ID, params));
		return searchResult.getResult();
	}

	@Override
	public SearchPageData<ProductConfigurationModel> getProductRelatedByThreshold(final Integer thresholdInDays,
			final int pageSize, final int currentPage)
	{
		final Date selectionDate = calculateModificationDate(thresholdInDays);
		final Map<String, Date> params = Collections.singletonMap("modifiedTime", selectionDate);
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(SELECT_PRODUCT_RELATED, params);
		final PaginatedFlexibleSearchParameter parameter = createPageableQuery(flexibleSearchQuery, pageSize, currentPage);
		return getPaginatedFlexibleSearchService().search(parameter);
	}

	@Override
	public SearchPageData<ProductConfigurationModel> getOrphaned(final int pageSize, final int currentPage)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(SELECT_ORPHANED);
		final PaginatedFlexibleSearchParameter parameter = createPageableQuery(flexibleSearchQuery, pageSize, currentPage);
		return getPaginatedFlexibleSearchService().search(parameter);
	}

	@Override
	public SearchPageData<ProductConfigurationModel> getAll(final int pageSize, final int currentPage)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(SELECT_ALL);
		final PaginatedFlexibleSearchParameter parameter = createPageableQuery(flexibleSearchQuery, pageSize, currentPage);
		return getPaginatedFlexibleSearchService().search(parameter);
	}

	@Override
	public boolean isOnlyRelatedToGivenEntry(final String configId, final String cartEntryKey)
	{
		validateParameterNotNull(configId, ARGUMENT_CONFIG_ID_CANNOT_BE_NULL);
		validateParameterNotNull(cartEntryKey, "Argument cartEntryKey cannot be null");
		final Map<String, String> params = new HashMap<>();
		params.put(PARAM_NAME_CONFIG_ID, configId);
		params.put("cartEntryKey", cartEntryKey);
		return getTotalCount(params, SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY) == 0
				&& getTotalCount(params, SELECT_BY_DRAFT_CONFIG_ID_AND_CART_ENTRY_KEY) == 0;
	}

	protected int getTotalCount(final Map<String, String> params, final String queryString)
	{
		final FlexibleSearchQuery flexibleSerchQuery = new FlexibleSearchQuery(queryString, params);
		final SearchResult<Object> search = getFlexibleSearchService().search(flexibleSerchQuery);
		final int totalCount = search.getTotalCount();
		return totalCount;
	}

	protected PaginatedFlexibleSearchParameter createPageableQuery(final FlexibleSearchQuery query, final int pageSize,
			final int currentPage)
	{
		final PaginatedFlexibleSearchParameter parameter = new PaginatedFlexibleSearchParameter();
		parameter.setFlexibleSearchQuery(query);
		final Map<String, String> sortMap = new LinkedHashMap();
		final SearchPageData searchPageData = PaginatedSearchUtils.createSearchPageDataWithPaginationAndSorting(pageSize,
				currentPage, true, sortMap);
		parameter.setSearchPageData(searchPageData);
		return parameter;
	}

	protected Date calculateModificationDate(final Integer thresholdInDays)
	{
		Preconditions.checkNotNull(thresholdInDays, "Threshold must not be null");
		final Calendar cal = Calendar.getInstance();
		return new Date(cal.getTime().toInstant().minus(thresholdInDays, ChronoUnit.DAYS).toEpochMilli());
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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

}
