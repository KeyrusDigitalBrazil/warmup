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

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DefaultPagedB2BUserGroupDao extends DefaultPagedGenericDao<B2BUserGroupModel>
{
	private static final String FIND_USERGROUP_BY_PARENT_UNIT = "SELECT {ug:pk} FROM { B2BUserGroup as ug JOIN B2BUnit as u"
			+ " ON {ug:unit} = {u:pk} }";

	private static final String DEFAULT_SORT_CODE = Config.getString(B2BConstants.DEFAULT_SORT_CODE_PROP, "byName");

	public DefaultPagedB2BUserGroupDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public SearchPageData<B2BUserGroupModel> find(final PageableData pageableData)
	{
		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData("byUnitName", FIND_USERGROUP_BY_PARENT_UNIT + " ORDER BY {u:name} "),
				createSortQueryData("byGroupID", FIND_USERGROUP_BY_PARENT_UNIT + " ORDER BY {ug:uid} "),
				createSortQueryData("byName", FIND_USERGROUP_BY_PARENT_UNIT + " ORDER BY {ug:name} "));

		return getPagedFlexibleSearchService().search(sortQueries, DEFAULT_SORT_CODE, new HashMap<String, Object>(), pageableData);
	}
}
