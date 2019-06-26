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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BCustomerService}
 *
 * @spring.bean b2bCustomerService
 */

public class DefaultB2BCustomerService implements B2BCustomerService<B2BCustomerModel, B2BUnitModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCustomerService.class);

	private UserService userService;
	private BaseDao baseDao;
	private SearchRestrictionService searchRestrictionService;

	@Override
	public void addMember(final PrincipalModel member, final PrincipalGroupModel group)
	{
		final HashSet<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(
				(member.getGroups() != null ? member.getGroups() : Collections.emptySet()));
		groups.add(group);
		member.setGroups(groups);
	}

	@Override
	public void setParentB2BUnit(final B2BCustomerModel member, final B2BUnitModel parentB2BUnit)
	{
		if (member.getDefaultB2BUnit() != null)
		{
			member.setDefaultB2BUnit(parentB2BUnit);
		}

		addMember(member, parentB2BUnit);
	}


	@Override
	public B2BCustomerModel getUserForUID(final String userId)
	{
		B2BCustomerModel customer = null;
		try
		{
			customer = getUserService().getUserForUID(userId, B2BCustomerModel.class);
		}
		catch (final UnknownIdentifierException | ClassMismatchException e) //NOSONAR
		{
			customer = null;
			LOG.error("Failed to get user.");
		}
		return customer;

	}

	@Override
	public B2BCustomerModel getCurrentB2BCustomer()
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		return (currentUser instanceof B2BCustomerModel) ? (B2BCustomerModel) currentUser : null;
	}

	@Override
	public List<B2BCustomerModel> getAllUsers()
	{
		return getBaseDao().findAll(-1, 0, B2BCustomerModel.class);
	}

	@Override
	public List<B2BUserGroupModel> getAllB2BUserGroups()
	{
		return getBaseDao().findAll(-1, 0, B2BUserGroupModel.class);
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public boolean principalExists(final String uid)
	{
		return userService.isUserExisting(uid);
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	protected BaseDao getBaseDao()
	{
		return baseDao;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}
}
