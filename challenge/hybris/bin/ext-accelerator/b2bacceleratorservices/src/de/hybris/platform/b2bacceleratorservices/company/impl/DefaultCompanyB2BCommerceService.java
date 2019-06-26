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
package de.hybris.platform.b2bacceleratorservices.company.impl;


import de.hybris.platform.b2b.company.B2BGroupCycleValidator;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceCostCenterService;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommercePermissionService;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUnitService;
import de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUserService;
import de.hybris.platform.b2b.dao.PagedB2BCustomerDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.process.approval.services.impl.DefaultB2BApprovalProcessService;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BPermissionService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.impl.DefaultB2BBudgetService;
import de.hybris.platform.b2b.strategies.B2BApprovalProcessLookUpStrategy;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A service which services organization management
 *
 * @deprecated Since 6.0. Use {@link DefaultB2BCommerceB2BUserGroupService}, {@link DefaultB2BBudgetService},
 *             {@link DefaultB2BCommerceCostCenterService}, {@link DefaultB2BCommercePermissionService},
 *             {@link DefaultB2BCommerceUnitService}, {@link DefaultB2BCommerceUserService} and
 *             {@link DefaultB2BApprovalProcessService} instead.
 */
@Deprecated
public class DefaultCompanyB2BCommerceService implements CompanyB2BCommerceService
{
	private UserService userService;
	private PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao;
	private PagedGenericDao<B2BUserGroupModel> pagedB2BUserGroupDao;
	private PagedGenericDao<B2BCostCenterModel> pagedB2BCostCenterDao;
	private PagedGenericDao<B2BBudgetModel> pagedB2BBudgetDao;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private B2BApproverService<B2BCustomerModel> b2BApproverService;
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService;
	private B2BCostCenterService b2BCostCenterService;
	private CommonI18NService commonI18NService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private ModelService modelService;
	private SessionService sessionService;
	private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BBudgetService;
	private SearchRestrictionService searchRestrictionService;
	private PagedGenericDao<B2BPermissionModel> pagedB2BPermissionDao;
	private B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2BPermissionService;
	private B2BApprovalProcessLookUpStrategy b2BApprovalProcessLookUpStrategy;
	private B2BGroupCycleValidator b2BGroupCycleValidator;
	private BusinessProcessService businessProcessService;

	private static final Logger LOG = Logger.getLogger(DefaultCompanyB2BCommerceService.class);

	@Override
	public B2BUnitModel getUnitForUid(final String unitUid)
	{
		return getB2BUnitService().getUnitForUid(unitUid);
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnit(final B2BUnitModel unit)
	{
		return (T) this.getB2BUnitService().getParent(unit);
	}

	@Override
	public <T extends B2BCustomerModel> T getCurrentUser()
	{
		return (T) this.getUserService().getCurrentUser();
	}

	@Override
	public <T extends B2BCustomerModel> List<T> getMembersOfUnitForUserGroup(final B2BUnitModel unit, final String userGroupId)
	{
		return (List<T>) this.getB2BUnitService().getUsersOfUserGroup(unit, userGroupId, true);
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedUsersForUserGroups(final PageableData pageableData,
			final String... userGroupUID)
	{
		return getPagedB2BCustomerDao().findPagedCustomersByGroupMembership(pageableData, userGroupUID);
	}

	@Override
	public Collection<? extends CurrencyModel> getAllCurrencies()
	{
		return getCommerceCommonI18NService().getAllCurrencies();
	}

	@Override
	public <T extends ItemModel> void saveModel(final T model)
	{
		try
		{
			getModelService().save(model);
		}
		catch (final ModelSavingException e)
		{
			if (e.getCause() instanceof InterceptorException
					&& ((InterceptorException) e.getCause()).getInterceptor().getClass().equals(UniqueAttributesInterceptor.class))
			{
				LOG.error("The uid of the model being stored already exists, could not save.");
				throw e;
			}
			else
			{
				throw e;
			}
		}
	}

	@Override
	public <T extends CurrencyModel> T getCurrencyForIsoCode(final String isoCode)
	{
		return (T) getCommonI18NService().getCurrency(isoCode);
	}

	@Override
	public <T extends B2BCustomerModel> T getCustomerForUid(final String uid)
	{
		return (T) getUserService().getUserForUID(uid, B2BCustomerModel.class);
	}

	@Override
	public B2BPermissionModel getPermissionForCode(final String permission)
	{
		return getB2BPermissionService().getB2BPermissionForCode(permission);
	}

	@Override
	public B2BUserGroupModel getB2BUserGroupForUid(final String uid)
	{
		return this.getUserService().getUserGroupForUID(uid, B2BUserGroupModel.class);
	}

	@Override
	public Map<String, String> getBusinessProcesses()
	{
		//TODO: pass in the BaseStoreModel which would hold a list of available process codes for the store.
		return this.getB2BApprovalProcessLookUpStrategy().getProcesses(null);
	}

	protected void addMemberToUserGroup(final UserGroupModel usergroup, final UserModel user)
	{
		final HashSet<PrincipalModel> members = new HashSet<PrincipalModel>(usergroup.getMembers());
		members.add(user);
		usergroup.setMembers(members);
	}

	protected void removedMemberFromUserGroup(final UserGroupModel usergroup, final UserModel user)
	{
		final HashSet<PrincipalModel> members = new HashSet<PrincipalModel>(usergroup.getMembers());
		members.remove(user);
		usergroup.setMembers(members);
	}

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2BCustomerService()
	{
		return b2BCustomerService;
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
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

	protected B2BApproverService<B2BCustomerModel> getB2BApproverService()
	{
		return b2BApproverService;
	}

	@Required
	public void setB2BApproverService(final B2BApproverService<B2BCustomerModel> b2BApproverService)
	{
		this.b2BApproverService = b2BApproverService;
	}

	@Required
	public void setB2BCustomerService(final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2BCustomerService)
	{
		this.b2BCustomerService = b2BCustomerService;
	}


	public B2BCostCenterService getB2BCostCenterService()
	{
		return b2BCostCenterService;
	}

	@Required
	public void setB2BCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		b2BCostCenterService = b2bCostCenterService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
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

	public B2BBudgetService<B2BBudgetModel, B2BCustomerModel> getB2BBudgetService()
	{
		return b2BBudgetService;
	}

	@Required
	public void setB2BBudgetService(final B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2bBudgetService)
	{
		b2BBudgetService = b2bBudgetService;
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

	public PagedGenericDao<B2BCostCenterModel> getPagedB2BCostCenterDao()
	{
		return pagedB2BCostCenterDao;
	}

	@Required
	public void setPagedB2BCostCenterDao(final PagedGenericDao<B2BCostCenterModel> pagedB2BCostCenterDao)
	{
		this.pagedB2BCostCenterDao = pagedB2BCostCenterDao;
	}

	public PagedGenericDao<B2BBudgetModel> getPagedB2BBudgetDao()
	{
		return pagedB2BBudgetDao;
	}

	@Required
	public void setPagedB2BBudgetDao(final PagedGenericDao<B2BBudgetModel> pagedB2BBudgetDao)
	{
		this.pagedB2BBudgetDao = pagedB2BBudgetDao;
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

	protected B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> getB2BPermissionService()
	{
		return b2BPermissionService;
	}

	@Required
	public void setB2BPermissionService(final B2BPermissionService<B2BCustomerModel, B2BPermissionResultModel> b2BPermissionService)
	{
		this.b2BPermissionService = b2BPermissionService;
	}

	public CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
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

	protected B2BApprovalProcessLookUpStrategy getB2BApprovalProcessLookUpStrategy()
	{
		return b2BApprovalProcessLookUpStrategy;
	}

	@Required
	public void setB2BApprovalProcessLookUpStrategy(final B2BApprovalProcessLookUpStrategy b2BApprovalProcessLookUpStrategy)
	{
		this.b2BApprovalProcessLookUpStrategy = b2BApprovalProcessLookUpStrategy;
	}

	protected B2BGroupCycleValidator getB2BGroupCycleValidator()
	{
		return b2BGroupCycleValidator;
	}

	@Required
	public void setB2BGroupCycleValidator(final B2BGroupCycleValidator b2BGroupCycleValidator)
	{
		this.b2BGroupCycleValidator = b2BGroupCycleValidator;
	}
}