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

import de.hybris.platform.b2b.company.B2BCommercePermissionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BCommercePermissionService}
 */
public class DefaultB2BCommercePermissionService implements B2BCommercePermissionService
{
	private PagedGenericDao<B2BPermissionModel> pagedB2BPermissionDao;
	private B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2bPermissionService;
	private UserService userService;
	private ModelService modelService;

	@Override
	public SearchPageData<B2BPermissionModel> getPagedPermissions(final PageableData pageableData)
	{
		return getPagedB2BPermissionDao().find(pageableData);
	}

	@Override
	public B2BPermissionModel getPermissionForCode(final String permissionCode)
	{
		return getB2bPermissionService().getB2BPermissionForCode(permissionCode);
	}

	@Override
	public B2BPermissionModel addPermissionToCustomer(final String user, final String permission)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<B2BPermissionModel> permissionModels = new HashSet<B2BPermissionModel>(customer.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.add(permissionModel);
		customer.setPermissions(permissionModels);
		getModelService().save(customer);
		return permissionModel;
	}

	@Override
	public B2BPermissionModel removePermissionFromCustomer(final String user, final String permission)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<B2BPermissionModel> permissionModels = new HashSet<B2BPermissionModel>(customer.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.remove(permissionModel);
		customer.setPermissions(permissionModels);
		getModelService().save(customer);
		return permissionModel;
	}

	@Override
	public B2BPermissionModel addPermissionToUserGroup(final String uid, final String permission)
	{
		ServicesUtil.validateParameterNotNull(permission, "Parameter [permission] may not be null.");

		// no need to validate that uid is not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(uid, B2BUserGroupModel.class);
		final List<B2BPermissionModel> permissionModels = new ArrayList<B2BPermissionModel>(userGroupModel.getPermissions());
		final B2BPermissionModel permissionModel = getB2bPermissionService().getB2BPermissionForCode(permission);
		permissionModels.add(permissionModel);
		userGroupModel.setPermissions(permissionModels);
		getModelService().save(userGroupModel);
		return permissionModel;
	}

	@Override
	public B2BPermissionModel removePermissionFromUserGroup(final String uid, final String permission)
	{
		ServicesUtil.validateParameterNotNull(permission, "Parameter [permission] may not be null.");

		// no need to validate that uid is not null, UserService does it
		final B2BUserGroupModel userGroupModel = getUserService().getUserGroupForUID(uid, B2BUserGroupModel.class);
		final List<B2BPermissionModel> permissionModels = new ArrayList<B2BPermissionModel>(userGroupModel.getPermissions());
		final B2BPermissionModel permissionModel = getB2bPermissionService().getB2BPermissionForCode(permission);
		permissionModels.remove(permissionModel);
		userGroupModel.setPermissions(permissionModels);
		getModelService().save(userGroupModel);
		return permissionModel;
	}

	protected PagedGenericDao<B2BPermissionModel> getPagedB2BPermissionDao()
	{
		return pagedB2BPermissionDao;
	}

	@Required
	public void setPagedB2BPermissionDao(final PagedGenericDao<B2BPermissionModel> pagedB2BPermissionDao)
	{
		this.pagedB2BPermissionDao = pagedB2BPermissionDao;
	}

	protected B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> getB2bPermissionService()
	{
		return b2bPermissionService;
	}

	@Required
	public void setB2bPermissionService(
			final B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2bPermissionService)
	{
		this.b2bPermissionService = b2bPermissionService;
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
