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
package de.hybris.platform.warehousing.util.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;

import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractWarehousingDao<T> implements WarehousingDao<T>
{
	private FlexibleSearchService flexibleSearchService;
	private String code;

	protected abstract String getQuery();

	@Override
	public T getByCode(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(getQuery());
		query.addQueryParameter(getCode(), code);
		return getFlexibleSearchService().searchUnique(query);
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

	protected String getCode()
	{
		return code;
	}

	@Required
	public void setCode(String code)
	{
		this.code = code;
	}
}
