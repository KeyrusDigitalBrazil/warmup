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
package de.hybris.platform.b2b.company.impl;

import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BCommerceB2BUserGroupService}
 */
public class DefaultB2BCommerceB2BUserGroupService implements B2BCommerceB2BUserGroupService
{

	private PagedGenericDao<B2BUserGroupModel> pagedB2BUserGroupDao;

	private UserService userService;

	private ModelService modelService;

	@Override
	public SearchPageData<B2BUserGroupModel> getPagedB2BUserGroups(final PageableData pageableData)
	{
		return getPagedB2BUserGroupDao().find(pageableData);
	}

	@Override
	public Set<PrincipalGroupModel> updateUserGroups(final Collection<String> availableUserGroups,
			final Collection<String> selectedUserGroups, final B2BCustomerModel customerModel)
	{
		final Set<PrincipalGroupModel> customerGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());

		// If you pass in NULL then nothing will happen
		if (selectedUserGroups != null)
		{
			for (final String group : availableUserGroups)
			{
				// add a group
				final UserGroupModel userGroupModel = getUserService().getUserGroupForUID(group);
				if (selectedUserGroups.contains(group))
				{
					customerGroups.add(userGroupModel);
				}
				else
				{ // remove a group
					customerGroups.remove(userGroupModel);
				}
			}
			customerModel.setGroups(customerGroups);
		}

		return customerGroups;
	}

	@Override
	public <T extends UserGroupModel> T getUserGroupForUID(final String uid, final Class<T> userGroupType)
	{
		try
		{
			return getUserService().getUserGroupForUID(uid, userGroupType);
		}
		catch (final UnknownIdentifierException uie)
		{
			return null;
		}
	}

	@Override
	public void disableUserGroup(final String uid)
	{
		// no need to validate that uid is not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(uid, B2BUserGroupModel.class);
		userGroupModel.setMembers(Collections.<PrincipalModel> emptySet());
		getModelService().save(userGroupModel);
	}

	@Override
	public void removeUserGroup(final String uid)
	{
		// no need to validate that uid is not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(uid, B2BUserGroupModel.class);
		getModelService().remove(userGroupModel);
	}


	@Override
	public B2BCustomerModel addMemberToUserGroup(final String usergroup, final String user)
	{
		// no need to validate that params are not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		addMemberToUserGroup(userGroupModel, customer);
		getModelService().save(userGroupModel);
		return customer;
	}

	@Override
	public B2BCustomerModel removeMemberFromUserGroup(final String usergroup, final String user)
	{
		// no need to validate that params are not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		removeMemberFromUserGroup(userGroupModel, customer);
		getModelService().save(userGroupModel);
		return customer;
	}

	protected void addMemberToUserGroup(final B2BUserGroupModel usergroup, final B2BCustomerModel user)
	{
		final HashSet<PrincipalModel> members = new HashSet<PrincipalModel>(usergroup.getMembers());
		members.add(user);
		usergroup.setMembers(members);
	}

	protected void removeMemberFromUserGroup(final B2BUserGroupModel usergroup, final B2BCustomerModel user)
	{
		final HashSet<PrincipalModel> members = new HashSet<PrincipalModel>(usergroup.getMembers());
		members.remove(user);
		usergroup.setMembers(members);
	}

	protected PagedGenericDao<B2BUserGroupModel> getPagedB2BUserGroupDao()
	{
		return pagedB2BUserGroupDao;
	}

	@Required
	public void setPagedB2BUserGroupDao(final PagedGenericDao<B2BUserGroupModel> pagedB2BUserGroupDao)
	{
		this.pagedB2BUserGroupDao = pagedB2BUserGroupDao;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
