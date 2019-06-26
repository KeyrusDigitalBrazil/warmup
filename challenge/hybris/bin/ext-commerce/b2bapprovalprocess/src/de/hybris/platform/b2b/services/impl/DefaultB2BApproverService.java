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

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.dao.PagedB2BCustomerDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the {@link B2BApproverService}.
 *
 * @spring.bean b2bApproverService
 */
public class DefaultB2BApproverService implements B2BApproverService<B2BCustomerModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BApproverService.class);

	private ModelService modelService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private UserService userService;
	private B2BCommerceUnitService b2bCommerceUnitService;
	private PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao;


	@Override
	public List<B2BCustomerModel> getAllApprovers(final B2BCustomerModel principal)
	{
		final List<B2BCustomerModel> allApprovers = getImmediateApprovers(principal);
		B2BUnitModel b2bUnit = getB2bUnitService().getParent(principal);
		allApprovers.addAll(getImmediateApprovers(b2bUnit));
		while (getB2bUnitService().getParent(b2bUnit) != null)
		{
			b2bUnit = getB2bUnitService().getParent(b2bUnit);
			allApprovers.addAll(getImmediateApprovers(b2bUnit));
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Customer %s has the following potentional approvers %s", principal.getUid(),
					principalListToUidString(allApprovers)));
		}

		//Only return  approvers that are in the b2bapprovergroup
		final UserGroupModel groupToCheckFor = getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
		CollectionUtils.filter(allApprovers, new Predicate()
		{
			@Override
			public boolean evaluate(final Object arg)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) arg;
				return userService.isMemberOfGroup(b2bCustomerModel, groupToCheckFor);
			}
		});

		return allApprovers;
	}

	@Override
	public List<B2BCustomerModel> getAllActiveApprovers(final B2BCustomerModel principal)
	{
		final List<B2BCustomerModel> allApprovers = getImmediateApprovers(principal);
		B2BUnitModel b2bUnit = getB2bUnitService().getParent(principal);
		allApprovers.addAll(getImmediateApprovers(b2bUnit));
		while (getB2bUnitService().getParent(b2bUnit) != null)
		{
			b2bUnit = getB2bUnitService().getParent(b2bUnit);
			allApprovers.addAll(getImmediateApprovers(b2bUnit));
		}


		//Only return active approvers that are in the b2bapprovergroup, otherwise inactive customers can be assigned as an approver for orders.
		final UserGroupModel groupToCheckFor = userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
		CollectionUtils.filter(allApprovers, new Predicate()
		{

			@Override
			public boolean evaluate(final Object arg)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) arg;
				return b2bCustomerModel.getActive().booleanValue();
			}
		});
		CollectionUtils.filter(allApprovers, new Predicate()
		{

			@Override
			public boolean evaluate(final Object arg)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) arg;
				return userService.isMemberOfGroup(b2bCustomerModel, groupToCheckFor);
			}
		});


		return allApprovers;
	}

	/**
	 * Gets the id's for a list of approvers.
	 *
	 * @param allApprovers
	 * @return String list of approver id's
	 */
	protected String principalListToUidString(final List<B2BCustomerModel> allApprovers)
	{

		final List<String> approvers = new ArrayList(allApprovers.size());
		for (final B2BCustomerModel b2bCustomerModel : allApprovers)
		{
			approvers.add(b2bCustomerModel.getUid());
		}
		return approvers.toString();

	}

	/**
	 * Gets the immediate approvers for either a B2BCustomer or B2BUnit (the principal) which also includes the members of
	 * the principal's approver groups.
	 *
	 * @param principal
	 *           the principal
	 * @return the immediate approvers - a list of {@link B2BCustomerModel}
	 */
	protected List<B2BCustomerModel> getImmediateApprovers(final PrincipalModel principal)
	{
		final List<B2BCustomerModel> allApprovers = new ArrayList<B2BCustomerModel>();

		if (principal instanceof B2BCustomerModel)
		{

			final B2BCustomerModel employee = (B2BCustomerModel) principal;
			allApprovers.addAll(employee.getApprovers());

			final Set<B2BUserGroupModel> b2bApproverGroups = employee.getApproverGroups();
			for (final B2BUserGroupModel b2bUserGroupModel : b2bApproverGroups)
			{
				allApprovers.addAll(b2bUnitService.getAllUserGroupMembersForType(b2bUserGroupModel, B2BCustomerModel.class));
			}
		}
		else if (principal instanceof B2BUnitModel)
		{
			allApprovers.addAll(((B2BUnitModel) principal).getApprovers());

			final Set<B2BUserGroupModel> b2bApproverGroups = ((B2BUnitModel) principal).getApproverGroups();
			for (final B2BUserGroupModel b2bUserGroupModel : b2bApproverGroups)
			{
				allApprovers.addAll(b2bUnitService.getAllUserGroupMembersForType(b2bUserGroupModel, B2BCustomerModel.class));
			}

		}

		return allApprovers;
	}

	@Override
	public List<UserModel> getAccountManagerApprovers(final PrincipalModel principal)
	{
		final List<UserModel> allApprovers = new ArrayList<UserModel>();

		if (principal instanceof B2BUnitModel)
		{
			B2BUnitModel parent = (B2BUnitModel) principal;
			Set<UserGroupModel> approverGroups = ((B2BUnitModel) principal).getAccountManagerGroups();

			while (parent != null && approverGroups != null && approverGroups.isEmpty())
			{
				parent = b2bUnitService.getParent(parent);

				if (parent != null && parent.getAccountManagerGroups() != null && parent.getAccountManagerGroups().isEmpty())
				{
					approverGroups = parent.getAccountManagerGroups();
					for (final UserGroupModel userGroupModel : approverGroups)
					{
						allApprovers.addAll(b2bUnitService.getAllUserGroupMembersForType(userGroupModel, UserModel.class));
					}
				}
			}
		}

		//Only return  approvers that are in the b2bapprovergroup
		final UserGroupModel groupToCheckFor = userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
		CollectionUtils.filter(allApprovers, new Predicate()
		{

			@Override
			public boolean evaluate(final Object arg)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) arg;
				return userService.isMemberOfGroup(b2bCustomerModel, groupToCheckFor);
			}
		});
		return allApprovers;
	}

	@Override
	public B2BCustomerModel addApproverToCustomer(final String user, final String approver)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final B2BCustomerModel approverModel = getUserService().getUserForUID(approver, B2BCustomerModel.class);
		//Ensure the approver is in the b2b approvers group
		if (approverModel.getGroups() != null
				&& !approverModel.getGroups().contains(getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)))
		{
			throw new IllegalArgumentException("User passed to be assigned as an approver is not in the approver group");
		}
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(customer.getApprovers());
		approvers.add(approverModel);
		customer.setApprovers(approvers);
		this.getModelService().saveAll(approverModel, customer);
		return approverModel;
	}

	@Override
	public B2BCustomerModel removeApproverFromCustomer(final String user, final String approver)
	{
		final B2BCustomerModel customer = getUserService().getUserForUID(user, B2BCustomerModel.class);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(customer.getApprovers());
		final B2BCustomerModel approverModel = getUserService().getUserForUID(approver, B2BCustomerModel.class);
		approvers.remove(approverModel);
		customer.setApprovers(approvers);
		this.getModelService().save(customer);
		return approverModel;
	}

	/**
	 * @deprecated Since 4.4.
	 */
	@Override
	@Deprecated
	public boolean isMemberOf(final PrincipalGroupModel principal, final String userGroupUid)
	{

		final Set<PrincipalGroupModel> groups = principal.getAllGroups();
		for (final PrincipalGroupModel principalGroupModel : groups)
		{
			if (principalGroupModel.getUid().equals(userGroupUid))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public B2BCustomerModel addApproverToUnit(final String unitUid, final String approverUid)
	{
		Assert.hasText(unitUid, "unitUid can not be empty!");
		Assert.hasText(approverUid, "approverUid can not be empty!");
		final B2BUnitModel unit = getB2bCommerceUnitService().getUnitForUid(unitUid);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(unit.getApprovers());
		final B2BCustomerModel approver = getUserService().getUserForUID(approverUid, B2BCustomerModel.class);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(approver.getGroups());
		groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));
		approver.setGroups(groups);
		approvers.add(approver);
		unit.setApprovers(approvers);
		getModelService().saveAll(approver, unit);
		return approver;
	}

	@Override
	public B2BCustomerModel removeApproverFromUnit(final String unitUid, final String approverUid)
	{
		Assert.hasText(unitUid, "unitUid can not be empty!");
		Assert.hasText(approverUid, "approverUid can not be empty!");
		final B2BUnitModel unit = getB2bCommerceUnitService().getUnitForUid(unitUid);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(unit.getApprovers());
		final B2BCustomerModel approver = getUserService().getUserForUID(approverUid, B2BCustomerModel.class);
		approvers.remove(approver);
		unit.setApprovers(approvers);
		getModelService().saveAll(approver, unit);
		return approver;
	}

	@Override
	public SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(final PageableData pageableData,
			final String unitUid, final String... usergroupUid)
	{
		return getPagedB2BCustomerDao().findPagedApproversForUnitByGroupMembership(pageableData, unitUid, usergroupUid);
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

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
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

	protected B2BCommerceUnitService getB2bCommerceUnitService()
	{
		return b2bCommerceUnitService;
	}

	@Required
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
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

}
