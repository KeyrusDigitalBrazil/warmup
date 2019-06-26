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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.Session;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Proxy method for {@link de.hybris.platform.b2b.services.impl.DefaultB2BUnitService}. Will be deprecated at 6.6.
 */
public class B2BUnitServiceProxy implements B2BUnitService<B2BUnitModel, B2BCustomerModel>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> defaultB2BUnitService;

	@Override
	public String getApprovalProcessCodeForUnit(final B2BUnitModel unit)
	{
		if (unit == null)
		{
			return null;
		}
		if (StringUtils.isNotBlank(unit.getApprovalProcessCode()))
		{
			return unit.getApprovalProcessCode();
		}
		return getApprovalProcessCodeForUnit(getParent(unit));
	}

	@Override
	public Set<B2BUnitModel> getAllUnitsOfOrganization(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getAllUnitsOfOrganization(unit);
	}

	@Override
	public Set<B2BUnitModel> getAllUnitsOfOrganization(final B2BCustomerModel customer)
	{
		return getDefaultB2BUnitService().getAllUnitsOfOrganization(customer);
	}

	@Override
	public <M extends PrincipalModel> Set<M> getAllUserGroupMembersForType(final UserGroupModel userGroup,
			final Class<M> memberType)
	{
		return getDefaultB2BUnitService().getAllUserGroupMembersForType(userGroup, memberType);
	}

	@Override
	public B2BUnitModel getParent(final B2BCustomerModel employee)
	{
		return getDefaultB2BUnitService().getParent(employee);
	}

	@Override
	public B2BUnitModel getParent(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getParent(unit);
	}

	@Override
	public B2BUnitModel getRootUnit(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getRootUnit(unit);
	}

	@Override
	public B2BUnitModel findUnitByUid(final String uid)
	{
		return getDefaultB2BUnitService().findUnitByUid(uid);
	}

	@Override
	public B2BUnitModel getUnitForUid(final String uid)
	{
		return getDefaultB2BUnitService().getUnitForUid(uid);
	}

	@Override
	public Set<B2BUnitModel> getBranch(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getBranch(unit);
	}

	@Override
	public Set<B2BCustomerModel> getCustomers(final Set<B2BUnitModel> branch)
	{
		return getDefaultB2BUnitService().getCustomers(branch);
	}

	@Override
	public void disableBranch(final B2BUnitModel unit)
	{
		getDefaultB2BUnitService().disableBranch(unit);
	}

	@Override
	public void enableBranch(final B2BUnitModel unit)
	{
		getDefaultB2BUnitService().enableBranch(unit);
	}

	@Override
	public List<B2BUnitModel> getAllParents(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getAllParents(unit);
	}

	@Override
	public void addMember(final B2BUnitModel group, final PrincipalModel member)
	{
		getDefaultB2BUnitService().addMember(group, member);
	}

	@Override
	public Set<B2BCustomerModel> getB2BCustomers(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getB2BCustomers(unit);
	}

	@Override
	public Set<B2BUnitModel> getB2BUnits(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getB2BUnits(unit);
	}

	@Override
	public void updateParentB2BUnit(final B2BUnitModel parentB2BUnit, final PrincipalModel member)
	{
		getDefaultB2BUnitService().updateParentB2BUnit(parentB2BUnit, member);
	}

	@Override
	public Collection<B2BCustomerModel> getUsersOfUserGroup(final B2BUnitModel unit, final String userGroupId)
	{
		return getDefaultB2BUnitService().getUsersOfUserGroup(unit, userGroupId);
	}

	@Override
	public Collection<B2BCustomerModel> getUsersOfUserGroup(final B2BUnitModel unit, final String userGroupId,
			final boolean recursive)
	{
		return getDefaultB2BUnitService().getUsersOfUserGroup(unit, userGroupId, recursive);
	}

	@Override
	public String findApprovalProcessCodeForUnit(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().findApprovalProcessCodeForUnit(unit);
	}

	@Override
	public List<String> getAllApprovalProcesses()
	{
		return getDefaultB2BUnitService().getAllApprovalProcesses();
	}

	@Override
	public EmployeeModel findAccountManagerForUnit(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().findAccountManagerForUnit(unit);
	}

	@Override
	public EmployeeModel getAccountManagerForUnit(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getAccountManagerForUnit(unit);
	}

	@Override
	public B2BUnitModel findUnitWithCreditLimit(final B2BUnitModel unit, final CurrencyModel currency)
	{
		return getDefaultB2BUnitService().findUnitWithCreditLimit(unit, currency);
	}

	@Override
	public B2BUnitModel getUnitWithCreditLimit(final B2BUnitModel unit)
	{
		return getDefaultB2BUnitService().getUnitWithCreditLimit(unit);
	}

	@Override
	public void updateBranchInSession(final Session session, final UserModel currentUser)
	{
		getDefaultB2BUnitService().updateBranchInSession(session, currentUser);
	}

	@Override
	public Set<String> getAllProcessDefinitionsNames()
	{
		return getDefaultB2BUnitService().getAllProcessDefinitionsNames();
	}

	@Override
	public void disableUnit(final B2BUnitModel unit)
	{
		getDefaultB2BUnitService().disableUnit(unit);
	}

	@Override
	public void enableUnit(final B2BUnitModel unit)
	{
		getDefaultB2BUnitService().enableUnit(unit);
	}

	@Override
	public void setCurrentUnit(final B2BCustomerModel customer, final B2BUnitModel unit)
	{
		getDefaultB2BUnitService().setCurrentUnit(customer, unit);
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getDefaultB2BUnitService()
	{
		return defaultB2BUnitService;
	}

	@Required
	public void setDefaultB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> defaultB2BUnitService)
	{
		this.defaultB2BUnitService = defaultB2BUnitService;
	}
}
