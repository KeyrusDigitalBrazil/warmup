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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.Session;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * A service around {@link B2BUnitModel}.
 *
 * @param <T>
 *           extension of {@link CompanyModel}
 * @param <U>
 *           extension of {@link UserModel}
 * @spring.bean b2bUnitService
 */
public interface B2BUnitService<T extends CompanyModel, U extends UserModel>
{
	/**
	 * Retrieves all units of an organization the <code>unit</code> belongs too.
	 *
	 * @param unit
	 *           A {@link B2BUnitModel} within an organization
	 * @return A list of all units within an organization.
	 * @deprecated Since 6.0. Use {@link #getBranch(T)} instead
	 */
	@Deprecated
	Set<T> getAllUnitsOfOrganization(T unit);

	/**
	 * Retrieves all units of an organization the <code>customer</code> belongs too.
	 *
	 * @param customer
	 *           the session user.
	 * @return the all units of organization
	 */
	Set<T> getAllUnitsOfOrganization(B2BCustomerModel customer);

	/**
	 * Retrieves all members of a User Group of the specified type
	 *
	 * @param userGroup
	 *           the user group
	 * @param memberType
	 *           the principle's member type
	 * @return the principle members
	 */
	<M extends PrincipalModel> Set<M> getAllUserGroupMembersForType(UserGroupModel userGroup, Class<M> memberType);


	/**
	 * Gets the parent unit of a user.
	 *
	 * @param employee
	 *           the employee
	 * @return the parent
	 */
	T getParent(final B2BCustomerModel employee);

	/**
	 * Gets the parent unit of a unit.
	 *
	 * @param unit
	 *           the child unit
	 * @return the parent unit or null if <code>unit</code> was the root unit.
	 */
	T getParent(final T unit);

	/**
	 * Gets the root unit.
	 *
	 * @param unit
	 *           the unit
	 * @return the root unit
	 */
	T getRootUnit(final T unit);

	/**
	 * Find unit by uid.
	 *
	 * @param uid
	 *           the uid
	 * @return the b2 b unit model
	 * @deprecated Since 4.4. Use {@link #getUnitForUid(String)} instead
	 */
	@Deprecated
	T findUnitByUid(String uid);

	/**
	 * Find unit by uid.
	 *
	 * @param uid
	 *           the uid
	 * @return the b2 b unit model
	 */
	T getUnitForUid(String uid);

	/**
	 * Gets the branch of a unit.
	 *
	 * @param unit
	 *           the unit
	 * @return the branch
	 */
	Set<T> getBranch(T unit);

	/**
	 * Gets the employees.
	 *
	 * @param branch
	 *           the branch
	 * @return the employees
	 */
	Set<U> getCustomers(Set<T> branch);

	/**
	 * Disable branch. Mark all units in the branch as active = false
	 *
	 * @param unit
	 *           the unit
	 */
	void disableBranch(T unit);

	/**
	 * Enable branch. Mark all unit in the branch as active = true.
	 *
	 * @param unit
	 *           the unit
	 */
	void enableBranch(T unit);

	/**
	 * Get all the parent units in the organization hierarchy all the way up to the root.
	 *
	 * @param unit
	 *           the unit
	 * @return A Set of all b2b units (CompanyModel)
	 */
	List<T> getAllParents(final T unit);

	/**
	 * Adds the member to a group. The model is not saved.
	 *
	 * @param group
	 *           the group
	 * @param member
	 *           the member
	 */
	void addMember(final T group, final PrincipalModel member);

	/**
	 * Gets the b2 b customers.
	 *
	 * @param unit
	 *           the unit
	 * @return the b2 b customers
	 */
	Set<U> getB2BCustomers(final T unit);

	/**
	 * Gets units that are children of the current unit.
	 *
	 * @param unit
	 *           the unit
	 * @return the b2 b units
	 */
	Set<T> getB2BUnits(final T unit);

	/**
	 * Set the parent unit of a given principal. The member is not saved.
	 *
	 * @param parentB2BUnit
	 *           the parent b2 b unit
	 * @param member
	 *           the member
	 */
	void updateParentB2BUnit(final T parentB2BUnit, final PrincipalModel member);

	/**
	 * Gets all members from the unit who belong to specified User Group
	 *
	 * @param unit
	 *           the unit
	 * @param userGroupId
	 *           the user group id
	 * @return A collection of users who are members of the <param>unit</param> with group <param>userGroupId</param>
	 * @deprecated Since 6.0. Use
	 *             {@link #getUsersOfUserGroup(de.hybris.platform.b2b.model.B2BUnitModel, String, boolean)}
	 */
	@Deprecated
	Collection<U> getUsersOfUserGroup(final T unit, final String userGroupId);

	/**
	 * Gets all members from the unit who belong to specified User Group
	 *
	 * @param unit
	 *           the unit
	 * @param userGroupId
	 *           the user group id
	 * @param recursive
	 *           If true the lookup of users will continue for unit parent until at least one is found.
	 *
	 * @return A collection of users who are members of the <param>unit</param> with group <param>userGroupId</param>
	 *
	 */
	Collection<B2BCustomerModel> getUsersOfUserGroup(final B2BUnitModel unit, final String userGroupId, final boolean recursive);

	/**
	 * Get approval process code from unit, checking parent units up the organization tree up to the root unit if process
	 * is not set in the <code>unit</code>
	 *
	 * @param unit
	 * @return approval process code
	 * @deprecated Since 4.4. Use {@link #getApprovalProcessCodeForUnit(CompanyModel)} instead
	 */
	@Deprecated
	String findApprovalProcessCodeForUnit(final T unit);

	/**
	 * Get approval process code from unit, checking parent units up the organization tree up to the root unit if process
	 * is not set in the <code>unit</code>
	 *
	 * @param unit
	 * @return approval process code
	 */
	String getApprovalProcessCodeForUnit(final T unit);

	/**
	 * Get All Approval Process codes defined in local.properties
	 *
	 * @return List of approval process codes
	 * @deprecated Since 6.0. Use {@link #getAllProcessDefinitionsNames()}
	 */
	@Deprecated
	List<String> getAllApprovalProcesses();

	/**
	 * Get salesrep from unit, checking parent units up the organization tree up to the root unit if salesrep is not set
	 * in the <code>unit</code>
	 *
	 * @param unit
	 * @return the account manager
	 * @deprecated Since 4.4. Use {@link #getAccountManagerForUnit(CompanyModel)} instead
	 */
	@Deprecated
	EmployeeModel findAccountManagerForUnit(final T unit);

	/**
	 * Get salesrep from unit, checking parent units up the organization tree up to the root unit if salesrep is not set
	 * in the <code>unit</code>
	 *
	 * @param unit
	 * @return the account manager
	 */
	EmployeeModel getAccountManagerForUnit(final T unit);

	/**
	 * Get unit with credit limit/credit check group value, checking parent units up the organization tree up to the root
	 * unit if credit limit / credit check group is not set in the <code>unit</code>
	 *
	 * @param unit
	 * @param currency
	 * @return unit with credit limit/check
	 *
	 * @deprecated Since 4.4. Use {@link #getUnitWithCreditLimit(CompanyModel)} instead
	 */
	@Deprecated
	T findUnitWithCreditLimit(final T unit, final CurrencyModel currency);

	/**
	 * Get unit with credit limit/credit check group value, checking parent units up the organization tree up to the root
	 * unit if credit limit / credit check group is not set in the <code>unit</code>
	 *
	 * @param unit
	 * @return unit with credit limit/check
	 */
	T getUnitWithCreditLimit(final T unit);

	/**
	 * Sets the branch for the current use in the session.
	 *
	 * @param session
	 * @param currentUser
	 */
	void updateBranchInSession(final Session session, final UserModel currentUser);


	/**
	 * Gets all process definition name registered with the
	 * {@link de.hybris.platform.processengine.definition.ProcessDefinitionFactory}
	 *
	 * @return A unique list of all Business process names.
	 */
	Set<String> getAllProcessDefinitionsNames();

	/**
	 * Disables a b2b unit
	 *
	 * @param unit
	 *           the unit
	 */
	void disableUnit(B2BUnitModel unit);

	/**
	 * Enables a b2b unit
	 *
	 * @param unit
	 *           the unit
	 */
	void enableUnit(B2BUnitModel unit);


	/**
	 * Assigns a default unit for a customer who may be a member of multiple units. and modifies a branch for this
	 * customers session based on the unit
	 *
	 * @param customer
	 *           The current customer
	 * @param unit
	 *           A {@link B2BUnitModel} to be assigned as the default for a customer
	 */
	void setCurrentUnit(B2BCustomerModel customer, B2BUnitModel unit);
}
