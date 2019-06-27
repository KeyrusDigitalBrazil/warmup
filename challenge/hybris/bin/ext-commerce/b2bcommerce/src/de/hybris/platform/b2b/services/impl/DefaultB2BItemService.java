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
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * @deprecated Since 4.4. Distribute functionality to hybris services or if does not exist find an appropriate place in the b2b
 *             services.
 */

@Deprecated
public class DefaultB2BItemService implements B2BItemService
{

	/** The base dao. */
	private BaseDao baseDao;

	/** The role groups. */
	protected List<String> roles;

	private SessionService sessionService;

	private SearchRestrictionService searchRestrictionService;


	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgetForCode(String)}
	 */
	@Override
	@Deprecated
	public B2BBudgetModel findB2BBudgetByCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(B2BBudgetModel.CODE, code, B2BBudgetModel.class);
	}

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BCustomerService#getUserForUID(String)}
	 */
	@Override
	@Deprecated
	public B2BCustomerModel findB2BCustomerByCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(B2BCustomerModel.UID, code, B2BCustomerModel.class);
	}


	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.servicelayer.user.UserService#getUserGroupForUID(String, Class)}
	 */
	@Override
	@Deprecated
	public B2BUserGroupModel findB2BUserGroupByCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(B2BUserGroupModel.UID, code, B2BUserGroupModel.class);
	}


	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.servicelayer.user.UserService#getUserGroupForUID(String, Class)}
	 */
	@Override
	@Deprecated
	public PrincipalGroupModel findPrincipalGroupByCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(PrincipalGroupModel.UID, code, PrincipalGroupModel.class);
	}


	/**
	 * @deprecated Since 4.4. {@link de.hybris.platform.b2b.services.B2BCostCenterService#getCostCenterForCode(String)}
	 */
	@Override
	@Deprecated
	public B2BCostCenterModel findB2BCostCenterByCode(final String code)
	{
		return getBaseDao().findFirstByAttribute(B2BCostCenterModel.CODE, code, B2BCostCenterModel.class);

	}

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#isBudgetExisting(String)} Checks whether
	 *             the budget exists regardless of visibility constraints
	 * @param code
	 * @return true is budget with this code exists
	 */
	@Override
	@Deprecated
	public boolean budgetExists(final String code)
	{

		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return Boolean.valueOf(findB2BBudgetByCode(code) != null);
			}
		})).booleanValue();

	}

	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BCostCenterService#isCostCenterExisting(String)} Checks
	 *             whether the cost center exists regardless of visibility constraints
	 * @param code
	 * @return true is budget with this code exists
	 */
	@Override
	@Deprecated
	public boolean costCenterExists(final String code)
	{
		return ((Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return Boolean.valueOf(findB2BCostCenterByCode(code) != null);
			}
		})).booleanValue();
	}


	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BBudgetService#getB2BBudgets()}
	 */
	@Override
	@Deprecated
	public Set<B2BBudgetModel> findAllB2BBudgets()
	{
		final HashSet<B2BBudgetModel> models = new HashSet<B2BBudgetModel>();
		CollectionUtils.addAll(models, findAllItems(B2BBudgetModel.class).iterator());
		return models;
	}


	/**
	 * @deprecated Since 4.4. Use {@link de.hybris.platform.b2b.services.B2BCustomerService#getAllUsers()}
	 */
	@Override
	@Deprecated
	public Set<B2BCustomerModel> findAllApprovers()
	{
		final HashSet<B2BCustomerModel> models = new HashSet<B2BCustomerModel>();
		CollectionUtils.addAll(models, findAllItems(B2BCustomerModel.class).iterator());
		return models;
	}



	/**
	 * @deprecated Since 4.4. {@link de.hybris.platform.b2b.services.B2BCustomerService#getAllB2BUserGroups()}
	 */
	@Override
	@Deprecated
	public Set<B2BUserGroupModel> findAllB2BUserGroups()
	{
		final HashSet<B2BUserGroupModel> models = new HashSet<B2BUserGroupModel>();
		CollectionUtils.addAll(models, findAllItems(B2BUserGroupModel.class).iterator());
		return models;
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public List<PrincipalGroupModel> findAllRoleGroups()
	{
		final ArrayList<PrincipalGroupModel> models = new ArrayList<PrincipalGroupModel>();
		for (final String group : roles)
		{
			models.add(findPrincipalGroupByCode(group));
		}
		return models;
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public <M extends ItemModel> List<M> findAllItems(final Class<M> model)
	{
		return getBaseDao().findAll(-1, 0, model);
	}


	protected List<String> getRoles()
	{
		return roles;
	}

	@Required
	public void setRoles(final List<String> roles)
	{
		this.roles = roles;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	/**
	 * @return the baseDao
	 */
	protected BaseDao getBaseDao()
	{
		return baseDao;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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
