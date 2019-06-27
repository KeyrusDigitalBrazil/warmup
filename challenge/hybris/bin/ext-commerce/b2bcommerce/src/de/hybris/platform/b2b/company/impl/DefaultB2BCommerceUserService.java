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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.b2b.dao.PagedB2BCustomerDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BCommerceUserService}
 */
public class DefaultB2BCommerceUserService implements B2BCommerceUserService
{
	private ModelService modelService;
	private UserService userService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao;

	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomers(final PageableData pageableData)
	{
		return getPagedB2BCustomerDao().find(pageableData);
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomersByGroupMembership(final PageableData pageableData,
			final String... userGroupUids)
	{
		return getPagedB2BCustomerDao().findPagedCustomersByGroupMembership(pageableData, userGroupUids);
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomersBySearchTermAndGroupMembership(final PageableData pageableData,
			final String searchTerm, final String... userGroupUids)
	{
		validateParameterNotNullStandardMessage("pageableData", pageableData);
		validateParameterNotNullStandardMessage("searchTerm", searchTerm);
		validateParameterNotNullStandardMessage("userGroupUid", userGroupUids);
		return getPagedB2BCustomerDao().findPagedCustomersBySearchTermAndGroupMembership(pageableData, searchTerm, userGroupUids);
	}

	@Override
	public B2BUserGroupModel addB2BUserGroupToCustomer(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		groups.add(userGroupModel);
		customer.setGroups(groups);
		getModelService().save(customer);
		return userGroupModel;
	}

	@Override
	public void removeB2BUserGroupFromCustomerGroups(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<PrincipalGroupModel> groupsWithoutUsergroup = removeUsergroupFromGroups(usergroup, customer.getGroups());
		customer.setGroups(groupsWithoutUsergroup);
		getModelService().save(customer);
	}

	@Override
	public B2BUserGroupModel deselectB2BUserGroupFromCustomer(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(usergroup, B2BUserGroupModel.class);
		groups.remove(userGroupModel);
		customer.setGroups(groups);
		getModelService().save(customer);
		return userGroupModel;
	}

	@Override
	public B2BCustomerModel removeUserRole(final String user, final String role)
	{
		final B2BCustomerModel customerModel = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final UserGroupModel userGroupModel = getUserService().getUserGroupForUID(role);
		final Set<PrincipalGroupModel> customerModelGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		customerModelGroups.remove(userGroupModel);
		customerModel.setGroups(customerModelGroups);
		this.getModelService().save(customerModel);
		return customerModel;
	}

	@Override
	public B2BCustomerModel addUserRole(final String user, final String role)
	{
		final B2BCustomerModel customerModel = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final UserGroupModel userGroupModel = getUserService().getUserGroupForUID(role);
		final Set<PrincipalGroupModel> customerModelGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		customerModelGroups.add(userGroupModel);
		customerModel.setGroups(customerModelGroups);
		getModelService().save(customerModel);
		return customerModel;
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnitForCustomer(final String uid)
	{
		return (T) getB2BUnitService().getParent(getUserService().getUserForUID(uid, B2BCustomerModel.class));
	}

	@Override
	public void disableCustomer(final String uid)
	{
		final B2BCustomerModel customerModel = getUserService().getUserForUID(uid, B2BCustomerModel.class);
		customerModel.setActive(Boolean.FALSE);
		getModelService().save(customerModel);
	}

	@Override
	public void enableCustomer(final String uid)
	{
		final B2BCustomerModel customerModel = getUserService().getUserForUID(uid, B2BCustomerModel.class);
		customerModel.setActive(Boolean.TRUE);
		getModelService().save(customerModel);
	}

	protected Set<PrincipalGroupModel> removeUsergroupFromGroups(final String usergroup, final Set<PrincipalGroupModel> groups)
	{
		final Set<PrincipalGroupModel> groupsWithoutUsergroup = new HashSet<PrincipalGroupModel>(groups);
		CollectionUtils.filter(groupsWithoutUsergroup, new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final PrincipalGroupModel group = (PrincipalGroupModel) object;
				return !StringUtils.equals(usergroup, group.getUid());
			}
		});
		return groupsWithoutUsergroup;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected PagedB2BCustomerDao<B2BCustomerModel> getPagedB2BCustomerDao()
	{
		return pagedB2BCustomerDao;
	}

	@Required
	public void setPagedB2BCustomerDao(final PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao)
	{
		this.pagedB2BCustomerDao = pagedB2BCustomerDao;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2BUnitService = b2bUnitService;
	}

}
