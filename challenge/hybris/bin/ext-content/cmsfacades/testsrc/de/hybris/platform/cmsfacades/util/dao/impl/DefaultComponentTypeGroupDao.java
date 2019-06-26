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
package de.hybris.platform.cmsfacades.util.dao.impl;

import de.hybris.platform.cms2.model.ComponentTypeGroupModel;
import de.hybris.platform.cmsfacades.util.dao.ComponentTypeGroupDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.springframework.beans.factory.annotation.Required;


public class DefaultComponentTypeGroupDao implements ComponentTypeGroupDao
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public ComponentTypeGroupModel getComponentTypeGroupByCode(final String code)
	{
		final String queryString = "SELECT {pk} FROM {ComponentTypeGroup} WHERE {code}=?code";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("code", code);
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

}
