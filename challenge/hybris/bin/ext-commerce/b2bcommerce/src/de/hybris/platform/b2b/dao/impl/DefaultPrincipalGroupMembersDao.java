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

import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultPrincipalGroupMembersDao implements PrincipalGroupMembersDao
{
	protected static final Logger LOG = Logger.getLogger(DefaultPrincipalGroupMembersDao.class);

	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Finds all members of a Principal Group of a given type. FlexibleSearch filters non specified type members so as
	 * not to have to iterate and instantiate entire collection to filter non desired types
	 */
	public <T extends PrincipalModel> List<T> findAllMembersByType(final UserGroupModel parent, final Class<T> memberType)
	{
		return findMembersByType(parent, memberType, -1, 1);
	}

	/**
	 * Finds members of a Principal Group of a given type. FlexibleSearch filters non specified type members so as not to
	 * have to iterate and instantiate entire collection to filter non desired types
	 */
	public <T extends PrincipalModel> List<T> findMembersByType(final UserGroupModel parent, final Class<T> memberType,
			final int count, final int start)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(" SELECT {m.pk} ");
		builder.append(" FROM { ");
		builder.append("  PrincipalGroupRelation as pg  ");
		builder.append("  JOIN PrincipalGroup as p ");
		builder.append("    ON {pg.target} = {p.pk} ");
		builder.append("  JOIN ").append(getModelService().getModelType(memberType)).append(" as m ");
		builder.append("    ON {pg.source} = {m.pk} ");
		builder.append(" } ");
		builder.append(" WHERE  {p.pk} = ?parent ");


		final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
		query.setCount(-1);
		query.setStart(0);
		query.getQueryParameters().put("parent", parent);

		final SearchResult<T> result = getFlexibleSearchService().search(query);
		return result.getResult();

	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
