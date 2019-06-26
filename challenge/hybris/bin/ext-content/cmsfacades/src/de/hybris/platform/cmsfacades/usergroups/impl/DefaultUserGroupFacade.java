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
package de.hybris.platform.cmsfacades.usergroups.impl;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.usergroups.service.UserGroupSearchService;
import de.hybris.platform.cmsfacades.data.UserGroupData;
import de.hybris.platform.cmsfacades.usergroups.UserGroupFacade;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link UserGroupFacade}
 */
public class DefaultUserGroupFacade implements UserGroupFacade
{
	private UserService userService;
	private Converter<UserGroupModel, UserGroupData> userGroupDataConverter;
	private UserGroupSearchService userGroupSearchService;

	@Override
	public UserGroupData getUserGroupById(final String uid) throws CMSItemNotFoundException
	{
		try
		{
			final UserGroupModel userGroup = getUserService().getUserGroupForUID(uid);
			return getUserGroupDataConverter().convert(userGroup);
		}
		catch (final UnknownIdentifierException e)
		{
			throw new CMSItemNotFoundException("UserGroup with id [" + uid + "] is not found", e);
		}
	}

	@Override
	public SearchResult<UserGroupData> findUserGroups(final String text, final PageableData pageableData)
	{
		final SearchResult<UserGroupModel> userGroupSearchResult = getUserGroupSearchService().findUserGroups(text, pageableData);
		return new SearchResultImpl<>(
				userGroupSearchResult.getResult().stream() //
				.map(userGroup -> getUserGroupDataConverter().convert(userGroup)).collect(Collectors.toList()), //
				userGroupSearchResult.getTotalCount(), //
				userGroupSearchResult.getRequestedCount(), //
				userGroupSearchResult.getRequestedStart());
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<UserGroupModel, UserGroupData> getUserGroupDataConverter()
	{
		return userGroupDataConverter;
	}

	@Required
	public void setUserGroupDataConverter(final Converter<UserGroupModel, UserGroupData> userGroupDataConverter)
	{
		this.userGroupDataConverter = userGroupDataConverter;
	}

	protected UserGroupSearchService getUserGroupSearchService()
	{
		return userGroupSearchService;
	}

	@Required
	public void setUserGroupSearchService(final UserGroupSearchService userGroupSearchService)
	{
		this.userGroupSearchService = userGroupSearchService;
	}

}
