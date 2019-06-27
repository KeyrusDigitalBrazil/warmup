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
 *
 */
package de.hybris.platform.ordermanagementfacades.search.dao.impl;


import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.core.model.ItemModel;

import java.util.Collection;
import java.util.Map;


/**
 * SearchByStatusPagedGenericDao is a specific dao that will apply IN operator on the status search params
 */
public class SearchByStatusPagedGenericDao<M extends ItemModel> extends DefaultPagedGenericDao<M>
{
	protected final String typeCode;

	public SearchByStatusPagedGenericDao(String typeCode)
	{
		super(typeCode);
		this.typeCode = typeCode;
	}

	@Override
	protected void appendWhereClausesToBuilder(final StringBuilder builder, final Map<String, ?> params)
	{
		if (params != null && !params.isEmpty())
		{
			builder.append("WHERE ");
			boolean firstParam = true;
			for (final Map.Entry entry : params.entrySet())
			{
				final String paramName = entry.getKey().toString();
				if (!firstParam)
				{
					builder.append("AND ");
				}

				if (params.get(paramName) instanceof Collection)
				{
					builder.append("{c:").append(paramName).append("} IN (?").append(paramName).append(")").append(' ');
				}
				else
				{
					builder.append("{c:").append(paramName).append("}=?").append(paramName).append(' ');
				}
				firstParam = false;
			}
		}
	}

}
