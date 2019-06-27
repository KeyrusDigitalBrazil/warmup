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
package de.hybris.platform.smarteditwebservices.configuration.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.smarteditwebservices.configuration.dao.SmarteditConfigurationDao;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the SmartEdit Configuration DAO, responsible for the persistence of {@link SmarteditConfigurationModel}
 */
public class DefaultSmarteditConfigurationDao implements SmarteditConfigurationDao
{

	public static final String SELECT_ALL_CONFIGURATIONS = String.format("SELECT {%s} FROM {%s}", SmarteditConfigurationModel.PK,
			SmarteditConfigurationModel._TYPECODE);

	public static final String SELECT_CONFIGURATION_BY_KEY = String.format("SELECT {%s} FROM {%s} WHERE {%s}=?key ",
			SmarteditConfigurationModel.PK, SmarteditConfigurationModel._TYPECODE, SmarteditConfigurationModel.KEY);

	private static final String CONFIGURATION_KEY = "key";

	private FlexibleSearchService flexibleSearchService;


	@Override
	public List<SmarteditConfigurationModel> loadAll()
	{

		final FlexibleSearchQuery query = new FlexibleSearchQuery(SELECT_ALL_CONFIGURATIONS);
		final SearchResult<SmarteditConfigurationModel> search = getFlexibleSearchService().search(query);
		return search.getResult();
	}

	@Override
	public SmarteditConfigurationModel findByKey(final String key)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(SELECT_CONFIGURATION_BY_KEY);
		query.addQueryParameter(CONFIGURATION_KEY, key);
		final SearchResult<SmarteditConfigurationModel> search = getFlexibleSearchService().search(query);
		if (search.getCount() == 0)
		{
			return null;
		}
		else
		{
			return search.getResult().get(0);
		}
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

}
