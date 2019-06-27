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
package de.hybris.platform.commerceservices.organization.strategies.impl;

import de.hybris.platform.commerceservices.organization.strategies.OrgUnitAuthorizationStrategy;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link OrgUnitAuthorizationStrategy} interface.
 */
public class DefaultOrgUnitAuthorizationStrategy implements OrgUnitAuthorizationStrategy
{
	private static final String CREATE_GROUPS = "commerceservices.organization.rights.create.groups";
	private static final String EDIT_GROUPS = "commerceservices.organization.rights.edit.groups";
	private static final String EDIT_PARENT_GROUPS = "commerceservices.organization.rights.edit.parent.groups";
	private static final String VIEW_GROUPS = "commerceservices.organization.rights.view.groups";

	private static final String COMMA = ",";

	private UserService userService;

	@Override
	public void validateCreatePermission(final UserModel user)
	{
		if (!isMemberOfAuthorizedGroup(user, getUserGroups(CREATE_GROUPS)))
		{
			throw new IllegalStateException("Not allowed to create. User: " + user.getUid());
		}
	}

	@Override
	public void validateEditPermission(final UserModel user)
	{
		if (!canEditUnit(user))
		{
			throw new IllegalStateException("Not allowed to edit. User: " + user.getUid());
		}
	}

	@Override
	public boolean canEditUnit(final UserModel user)
	{
		return isMemberOfAuthorizedGroup(user, getUserGroups(EDIT_GROUPS));
	}

	@Override
	public void validateViewPermission(final UserModel user)
	{
		if (!isMemberOfAuthorizedGroup(user, getUserGroups(VIEW_GROUPS)))
		{
			throw new IllegalStateException("Not allowed to view. User: " + user.getUid());
		}
	}

	@Override
	public void validateEditParentPermission(final UserModel user)
	{
		if (!canEditParentUnit(user))
		{
			throw new IllegalStateException("Not allowed to edit parent unit. User: " + user.getUid());
		}
	}

	@Override
	public boolean canEditParentUnit(final UserModel user)
	{
		return isMemberOfAuthorizedGroup(user, getUserGroups(EDIT_PARENT_GROUPS));
	}

	protected List<String> getUserGroups(final String propertyKey)
	{
		final String groupsList = Config.getString(propertyKey, StringUtils.EMPTY);
		if (StringUtils.isBlank(groupsList))
		{
			throw new IllegalStateException("Property is empty or not configured. Property name: " + propertyKey);
		}
		return Arrays.asList(StringUtils.split(groupsList, COMMA));
	}

	protected boolean isMemberOfAuthorizedGroup(final UserModel user, final List<String> authorizedGroups)
	{
		if (getUserService().isMemberOfGroup(user, getUserService().getAdminUserGroup()))
		{
			return true;
		}
		if (CollectionUtils.isNotEmpty(authorizedGroups))
		{
			for (final String allowedGroup : authorizedGroups)
			{
				if (getUserService().isMemberOfGroup(user, getUserService().getUserGroupForUID(allowedGroup)))
				{
					return true;
				}
			}
		}
		return false;
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
}
