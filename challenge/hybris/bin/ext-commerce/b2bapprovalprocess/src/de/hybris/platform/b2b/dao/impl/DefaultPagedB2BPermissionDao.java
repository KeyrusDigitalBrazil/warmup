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
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.internal.dao.SortParameters;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class DefaultPagedB2BPermissionDao extends DefaultPagedGenericDao<B2BPermissionModel>
{
	private static final String FIND_PERMISSION_BY_PARENT_UNIT = "SELECT {B2BPermission:pk} FROM { B2BPermission as B2BPermission"
			+ " JOIN B2BUnit as B2BUnit ON  {B2BPermission:unit} = {B2BUnit:pk} } ORDER BY {B2BUnit:name}";

	private static final String DEFAULT_SORT_CODE = Config.getString(B2BConstants.DEFAULT_SORT_CODE_PROP, "byName");

	public DefaultPagedB2BPermissionDao(final String typeCode)
	{
		super(typeCode);
	}

	@Override
	public SearchPageData<B2BPermissionModel> find(final PageableData pageableData)
	{
		final List<SortQueryData> sortQueries = Arrays.asList(createSortQueryData("byUnitName", FIND_PERMISSION_BY_PARENT_UNIT),
				createSortQueryData("byName", new HashMap<String, Object>(),
						SortParameters.singletonAscending(B2BPermissionModel.CODE)));
		return getPagedFlexibleSearchService().search(sortQueries, DEFAULT_SORT_CODE, new HashMap<String, Object>(), pageableData);
	}
}
