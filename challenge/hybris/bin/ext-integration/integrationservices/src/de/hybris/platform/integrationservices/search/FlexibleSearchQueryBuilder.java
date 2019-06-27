/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.search;

import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.getItemAlias;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * A builder for constructing a {@link de.hybris.platform.servicelayer.search.FlexibleSearchQuery}
 */
public class FlexibleSearchQueryBuilder
{
	private static final String ORDER_BY_PK = " ORDER BY {%s:" + ItemModel.PK + "}";

	private final Map<String, Object> parameters = new HashMap<>();
	private final IntegrationObjectService integrationObjectService;
	private IntegrationObjectItemModel itemModel;
	private Map<String, Object> dataItem;
	private Integer start;
	private Integer count;
	private boolean totalCountNeeded;
	private boolean orderedByPK;
	private WhereClauseConditions filter;
	private ItemTypeMatch itemTypeMatch;

	public FlexibleSearchQueryBuilder(final IntegrationObjectService service)
	{
		Preconditions.checkArgument(service != null, "Implementation of IntegrationObjectService cannot be null");
		integrationObjectService = service;
	}

	public FlexibleSearchQueryBuilder withIntegrationObjectItem(final String objectCode, final String itemCode)
	{
		final IntegrationObjectItemModel model = integrationObjectService.findAllIntegrationObjectItems(objectCode).stream()
				.filter(it -> Objects.equals(itemCode, it.getCode()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Integration object '" + objectCode
						+ "' does not contain items with code '" + itemCode + "'"));
		return withIntegrationObjectItem(model);
	}

	public FlexibleSearchQueryBuilder withIntegrationObjectItem(final IntegrationObjectItemModel model)
	{
		itemModel = model;
		return this;
	}

	public FlexibleSearchQueryBuilder withKeyConditionFor(final Map<String, Object> obj)
	{
		dataItem = obj;
		return this;
	}

	public FlexibleSearchQueryBuilder withParameter(final String name, final Object value)
	{
		parameters.put(name, value);
		return this;
	}

	public FlexibleSearchQueryBuilder withStart(final Integer start)
	{
		this.start = start;
		return this;
	}

	public FlexibleSearchQueryBuilder withCount(final Integer count)
	{
		this.count = count;
		return this;
	}

	public FlexibleSearchQueryBuilder withTotalCount()
	{
		return withTotalCount(true);
	}

	public FlexibleSearchQueryBuilder withTotalCount(final boolean value)
	{
		totalCountNeeded = value;
		return this;
	}

	public FlexibleSearchQueryBuilder withFilter(final WhereClauseConditions filter)
	{
		this.filter = filter;
		return this;
	}

	public FlexibleSearchQueryBuilder withTypeHierarchyRestriction(final ItemTypeMatch itemTypeMatch)
	{
		this.itemTypeMatch = itemTypeMatch;
		return this;
	}

	public FlexibleSearchQueryBuilder orderedByPK()
	{
		orderedByPK = true;
		return this;
	}

	public FlexibleSearchQuery build()
	{
		final String searchQuery = selectFrom() + whereClause() + orderByPKClause();
		final FlexibleSearchQuery query = new FlexibleSearchQuery(searchQuery, parameters);
		if (start != null)
		{
			query.setStart(start);
		}
		if (count != null)
		{
			query.setCount(count);
		}
		query.setNeedTotal(totalCountNeeded);
		return query;
	}

	protected String selectFrom()
	{
		return FromClauseBuilder.builder()
				.withFilter(filter)
				.withIntegrationObjectItem(itemModel)
				.withTypeHierarchyRestriction(itemTypeMatch)
				.build();
	}

	protected String whereClause()
	{
		return WhereClauseBuilder.builder()
				.withParameters(parameters)
				.withIntegrationObjectItem(itemModel)
				.withDataItem(dataItem)
				.withFilter(filter)
				.build();
	}

	protected String orderByPKClause()
	{
		return orderedByPK ? String.format(ORDER_BY_PK, getItemAlias(itemModel)) : "";
	}
}
