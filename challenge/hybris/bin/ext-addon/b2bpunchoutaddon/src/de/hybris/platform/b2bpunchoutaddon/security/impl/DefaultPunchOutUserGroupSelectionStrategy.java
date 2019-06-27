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
package de.hybris.platform.b2bpunchoutaddon.security.impl;

import de.hybris.platform.b2bpunchoutaddon.security.PunchOutUserGroupSelectionStrategy;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Default implementation of {@link PunchOutUserGroupSelectionStrategy}.
 */
public class DefaultPunchOutUserGroupSelectionStrategy implements PunchOutUserGroupSelectionStrategy
{

	private UserService userService;
	private List<String> userGroupIDs;

	@Override
	public Collection<UserGroupModel> select(final String userId)
	{
		final List<UserGroupModel> result = new ArrayList<UserGroupModel>();
		for (final String userGroup : getUserGroupIDs())
		{
			result.add(userService.getUserGroupForUID(userGroup));
		}
		return result;
	}

	public List<String> getUserGroupIDs()
	{
		return userGroupIDs;
	}

	public void setUserGroupsIDs(final List<String> userGroups)
	{
		this.userGroupIDs = userGroups;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
