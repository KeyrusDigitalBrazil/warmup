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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Default implementation of the {@link B2BUnitDao}
 *
 * @spring.bean b2bUnitDao
 */
public class DefaultB2BUnitDao extends DefaultGenericDao<B2BUnitModel> implements B2BUnitDao
{
	public DefaultB2BUnitDao()
	{
		super(B2BUnitModel._TYPECODE);
	}

	/**
	 * Finds member of b2bunit who are also in the group specified by userGroupId
	 */
	public List<B2BCustomerModel> findB2BUnitMembersByGroup(final B2BUnitModel unit, final String userGroupId)
	{
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {c:pk}	");
		sql.append("FROM	");
		sql.append("{	");
		sql.append(B2BUnitModel._TYPECODE).append(" as unit	");
		sql.append("	JOIN PrincipalGroupRelation as unit_rel	");
		sql.append("	ON   {unit_rel:target} = {unit:pk} 	");
		sql.append("	JOIN B2BCustomer as c	");
		sql.append("	ON   {c:pk} = {unit_rel:source}	");
		sql.append("	JOIN PrincipalGroupRelation as group_rel	");
		sql.append("	ON   {group_rel:source} = {c:pk} 	");
		sql.append("	JOIN UserGroup as user_group	");
		sql.append("	ON   {user_group:pk} = {group_rel:target}	");
		sql.append("}	");
		sql.append("WHERE {unit:pk} = ?unit	");
		sql.append("AND   {user_group:uid} = ?uid	");

		final Map<String, Object> attr = new HashMap<String, Object>(2);
		attr.put("unit", unit);
		attr.put("uid", userGroupId);

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);

		final SearchResult<B2BCustomerModel> result = this.getFlexibleSearchService().search(query);

		return result.getResult();


	}
}
