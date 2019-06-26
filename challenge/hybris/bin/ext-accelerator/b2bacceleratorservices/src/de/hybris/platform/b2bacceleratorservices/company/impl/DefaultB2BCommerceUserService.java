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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUserService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of B2B Commerce User Service.
 *
 * @deprecated Since 6.4. Use {@link de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUserService} instead.
 */
@Deprecated
public class DefaultB2BCommerceUserService extends DefaultCompanyB2BCommerceService implements B2BCommerceUserService
{
	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomers(final PageableData pageableData)
	{
		return getPagedB2BCustomerDao().find(pageableData);
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomersByGroupMembership(final PageableData pageableData,
			final String... userGroupUid)
	{
		return getPagedB2BCustomerDao().findPagedCustomersByGroupMembership(pageableData, userGroupUid);
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedCustomersBySearchTermAndGroupMembership(final PageableData pageableData,
			final String searchTerm, final String... userGroupUid)
	{
		throw new NotImplementedException(
				"Not implemented. Use de.hybris.platform.b2b.company.impl.DefaultB2BCommerceUserService.getPagedCustomersBySearchTermAndGroupMembership(PageableData, String, String[]).");
	}

	@Override
	public B2BUserGroupModel addB2BUserGroupToCustomer(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(usergroup);
		groups.add(userGroupModel);
		customer.setGroups(groups);
		this.getModelService().save(customer);
		return userGroupModel;
	}

	/**
	 * Remove Usergroups @link B2BCustomerModel} object for a given unique id of a customer
	 *
	 * @param user
	 *           unique identifier for {@link B2BCustomerModel} representing a customer
	 * @param usergroup
	 *           unique identifier for {@link B2BUserGroupModel} representing a user group
	 * @return updated {@link B2BUserGroupModel} object with user group removed from the given unique identifier for
	 *         {@link B2BUserGroupModel}
	 *
	 * @deprecated Since 5.0. Use deselectB2BUserGroupFromCustomer(String user, String usergroup) or
	 *             removeB2BUserGroupToCustomer(String user, String usergroup) instead.
	 */
	@Deprecated
	@Override
	public B2BUserGroupModel removeB2BUserGroupToCustomer(final String user, final String usergroup)
	{
		return deselectB2BUserGroupFromCustomer(user, usergroup);
	}

	@Override
	public B2BUserGroupModel deselectB2BUserGroupFromCustomer(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customer.getGroups());
		final B2BUserGroupModel userGroupModel = this.getB2BUserGroupForUid(usergroup);
		groups.remove(userGroupModel);
		customer.setGroups(groups);
		this.getModelService().save(customer);
		return userGroupModel;
	}

	@Override
	public void removeB2BUserGroupFromCustomerGroups(final String user, final String usergroup)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<PrincipalGroupModel> groupsWithoutUsergroup = removeUsergroupFromGroups(usergroup, customer.getGroups());
		customer.setGroups(groupsWithoutUsergroup);
		this.getModelService().save(customer);
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

	@Override
	public B2BCustomerModel addApproverToCustomer(final String user, final String approver)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(customer.getApprovers());
		final B2BCustomerModel approverModel = getCustomerForUid(approver);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(approverModel.getGroups());
		groups.add(getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP));
		approverModel.setGroups(groups);
		approvers.add(approverModel);
		customer.setApprovers(approvers);
		this.getModelService().saveAll(approverModel, customer);
		return approverModel;
	}

	@Override
	public B2BCustomerModel removeApproverFromCustomer(final String user, final String approver)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<B2BCustomerModel> approvers = new HashSet<B2BCustomerModel>(customer.getApprovers());
		final B2BCustomerModel approverModel = getCustomerForUid(approver);
		approvers.remove(approverModel);
		customer.setApprovers(approvers);
		this.getModelService().save(customer);
		return approverModel;
	}

	@Override
	public B2BCustomerModel removeUserRole(final String user, final String role)
	{
		final B2BCustomerModel customerModel = getCustomerForUid(user);
		validateParameterNotNullStandardMessage("usser", user);
		final UserGroupModel userGroupModel = this.getUserService().getUserGroupForUID(role);
		validateParameterNotNullStandardMessage("user group", role);
		final Set<PrincipalGroupModel> customerModelGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		customerModelGroups.remove(userGroupModel);
		customerModel.setGroups(customerModelGroups);
		this.getModelService().save(customerModel);
		return customerModel;
	}

	@Override
	public B2BCustomerModel addUserRole(final String user, final String role)
	{
		final B2BCustomerModel customerModel = getCustomerForUid(user);
		validateParameterNotNullStandardMessage("user", user);
		final UserGroupModel userGroupModel = this.getUserService().getUserGroupForUID(role);
		validateParameterNotNullStandardMessage("user group", role);
		final Set<PrincipalGroupModel> customerModelGroups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		customerModelGroups.add(userGroupModel);
		customerModel.setGroups(customerModelGroups);
		this.getModelService().save(customerModel);
		return customerModel;
	}

	@Override
	public B2BPermissionModel removePermissionFromCustomer(final String user, final String permission)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<B2BPermissionModel> permissionModels = new HashSet<B2BPermissionModel>(customer.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.remove(permissionModel);
		customer.setPermissions(permissionModels);
		this.getModelService().save(customer);
		return permissionModel;
	}

	@Override
	public B2BPermissionModel addPermissionToCustomer(final String user, final String permission)
	{
		final B2BCustomerModel customer = this.getCustomerForUid(user);
		final Set<B2BPermissionModel> permissionModels = new HashSet<B2BPermissionModel>(customer.getPermissions());
		final B2BPermissionModel permissionModel = getPermissionForCode(permission);
		permissionModels.add(permissionModel);
		customer.setPermissions(permissionModels);
		this.getModelService().save(customer);
		return permissionModel;
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnitForCustomer(final String uid)
	{
		return (T) getB2BUnitService().getParent(getCustomerForUid(uid));
	}

	@Override
	public void disableCustomer(final String uid)
	{
		final B2BCustomerModel customerModel = getCustomerForUid(uid);
		validateParameterNotNullStandardMessage("B2BCustomer", uid);
		customerModel.setActive(Boolean.FALSE);
		this.saveModel(customerModel);
	}

	@Override
	public void enableCustomer(final String uid)
	{
		final B2BCustomerModel customerModel = getCustomerForUid(uid);
		validateParameterNotNullStandardMessage("B2BCustomer", uid);
		customerModel.setActive(Boolean.TRUE);
		this.saveModel(customerModel);
	}
}
