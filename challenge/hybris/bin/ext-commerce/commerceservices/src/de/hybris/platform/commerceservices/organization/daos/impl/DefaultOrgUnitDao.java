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
package de.hybris.platform.commerceservices.organization.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link OrgUnitDao} interface extending {@link DefaultPagedGenericDao}.
 */
public class DefaultOrgUnitDao<T extends OrgUnitModel> extends DefaultPagedGenericDao<T> implements OrgUnitDao<T>
{
	/**
	 * Sub-query that makes sure only the latest version for each distinct quote code is returned.
	 */
	protected static final String QUOTE_DISTINCT_CODE_MAX_VERSION_QUERY = " AND EXISTS ( SELECT CODE_MAX_VERSION.code, CODE_MAX_VERSION.maxVersion FROM"
			+ " ({{ SELECT {code} as code, max({version}) as maxVersion FROM {Quote} WHERE {state} IN (?states) GROUP BY {code} }})"
			+ " CODE_MAX_VERSION WHERE {quote:code} = CODE_MAX_VERSION.code AND {quote:version} = CODE_MAX_VERSION.maxVersion )";

	/**
	 * Joining the quote table with other tables (e.g. PrincipipalGroupRelation), so that all quotes associated to the
	 * employee's organizational units can be resolved.
	 */
	protected static final String QUOTE_TO_EMPLOYEE_JOINS = " JOIN Customer as cust ON {quote:user} = {cust:pk}"
			+ " JOIN PrincipalGroupRelation as rel1 ON {rel1:source} = {cust:pk}"
			+ " JOIN OrgUnit as unit ON {unit:pk} = {rel1:target}"
			+ " JOIN PrincipalGroupRelation as rel2 ON {rel2:target} = {unit:pk}"
			+ " JOIN Employee as empl ON {empl:pk} = {rel2:source}";

	/**
	 * Query template to retrieve quotes associated to an employee. Takes a JOIN statement used to define the relation
	 * between quote and employee as a first argument and an additional clause to narrow down the result set as a second
	 * argument.
	 */
	protected static final String QUOTE_FOR_EMPLOYEE_QUERY_FORMAT = "SELECT {quote:pk} FROM { Quote as quote %s } WHERE {empl:pk} = ?employee AND {quote:state} IN (?states) %s";

	public DefaultOrgUnitDao(final String typeCode)
	{
		super(typeCode);
	}

	private ModelService modelService;

	@Override
	public <U extends OrgUnitModel> List<U> findRootUnits(final Class<U> type, final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("type", type);
		validateParameterNotNullStandardMessage("pageableData", pageableData);

		final String modelType = getModelService().getModelType(type);
		final StringBuilder sql = new StringBuilder();

		// Query for units of the given type that are not a member of another unit of the same type
		sql.append("SELECT DISTINCT {unit:pk} ");
		sql.append("FROM {").append(modelType).append(" AS unit} ");
		sql.append("WHERE NOT EXISTS");
		sql.append("(");
		sql.append("	{{ ");
		sql.append("		SELECT {target:pk} FROM ");
		sql.append("		{ ");
		sql.append("			PrincipalgroupRelation AS rel ");
		sql.append("			JOIN ").append(modelType).append(" AS src ");
		sql.append("			ON {rel:source}={src:pk} ");
		sql.append("			JOIN ").append(modelType).append(" AS target ");
		sql.append("			ON {target:pk}={rel:target} ");
		sql.append("		} ");
		sql.append("		WHERE {rel:source}={unit:pk} ");
		sql.append("	}} ");
		sql.append(")");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());

		final SearchPageData<U> searchPageData = getPagedFlexibleSearchService().search(query, pageableData);
		return searchPageData.getResults();
	}

	@Override
	public <U extends PrincipalModel> SearchPageData<U> findMembersOfType(final OrgUnitModel unit, final Class<U> memberType,
			final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("unit", unit);
		validateParameterNotNullStandardMessage("memberType", memberType);
		validateParameterNotNullStandardMessage("pageableData", pageableData);

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT {m:pk} ");
		sql.append("FROM ");
		sql.append("{ ");
		sql.append("OrgUnit AS unit ");
		sql.append("	JOIN PrincipalGroupRelation AS unit_rel ");
		sql.append("	ON   {unit_rel:target} = {unit:pk} ");
		sql.append("	JOIN ").append(getModelService().getModelType(memberType)).append(" AS m ");
		sql.append("	ON   {m:pk} = {unit_rel:source} ");
		sql.append("} ");
		sql.append("WHERE {unit:pk} = ?unit");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().put("unit", unit);

		return getPagedFlexibleSearchService().search(query, pageableData);
	}

	@Override
	public <U extends PrincipalModel> SearchPageData<U> findMembersOfType(final Class<U> memberType,
			final PageableData pageableData, final String... orgUnitUids)
	{
		validateParameterNotNullStandardMessage("memberType", memberType);
		validateParameterNotNullStandardMessage("pageableData", pageableData);
		validateParameterNotNullStandardMessage("orgUnitUids", orgUnitUids);

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT {m:pk} ");
		sql.append("FROM ");
		sql.append("{ ");
		sql.append("OrgUnit AS unit ");
		sql.append("	JOIN PrincipalGroupRelation AS unit_rel ");
		sql.append("	ON   {unit_rel:target} = {unit:pk} ");
		sql.append("	JOIN ").append(getModelService().getModelType(memberType)).append(" AS m ");
		sql.append("	ON   {m:pk} = {unit_rel:source} ");
		sql.append("} ");
		sql.append("WHERE {unit:uid} IN (?orgUnitUids)");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().put("orgUnitUids", Arrays.asList(orgUnitUids));

		return getPagedFlexibleSearchService().search(query, pageableData);
	}

	@Override
	public SearchPageData<QuoteModel> findQuotesForEmployee(final EmployeeModel employee, final Set<QuoteState> states,
			final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("employee", employee);
		validateParameterNotNullStandardMessage("states", states);
		validateParameterNotNullStandardMessage("pageableData", pageableData);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(getQuoteForEmployeeQuery());
		query.getQueryParameters().put("employee", employee);
		query.getQueryParameters().put("states", states);

		return getPagedFlexibleSearchService().search(query, pageableData);
	}

	@Override
	public <U extends OrgUnitModel> SearchPageData<U> findAllUnits(final List<U> units, final PageableData pageableData)
	{
		// throw exception if units is null, return empty search page if units is empty.
		validateParameterNotNullStandardMessage("units", units);
		validateParameterNotNullStandardMessage("pageableData", pageableData);
		if (CollectionUtils.isEmpty(units))
		{
			return CommerceSearchUtils.createEmptySearchPageData();
		}

		final Iterator<U> iterator = units.iterator();
		final U next = iterator.next();
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT {unit:pk} ");
		sql.append("FROM {").append(getModelService().getModelType(next.getClass())).append(" AS unit} ");
		sql.append("WHERE {unit:path} LIKE '").append(next.getPath()).append("%'");
		while (iterator.hasNext())
		{
			sql.append(" OR {unit:path} LIKE '").append(iterator.next().getPath()).append("%'");
		}

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		return getPagedFlexibleSearchService().search(query, pageableData);
	}

	protected String getQuoteForEmployeeQuery()
	{
		return String.format(QUOTE_FOR_EMPLOYEE_QUERY_FORMAT, QUOTE_TO_EMPLOYEE_JOINS, QUOTE_DISTINCT_CODE_MAX_VERSION_QUERY);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
