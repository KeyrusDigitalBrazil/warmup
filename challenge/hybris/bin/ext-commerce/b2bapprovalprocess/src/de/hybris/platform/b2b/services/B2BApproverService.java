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

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;



/**
 * The Interface B2BApproverService. Service is responsible for fetching approvers related to a B2BCustomer.
 *
 * @param <T>
 *           the principal (customer)
 * @spring.bean b2bApproverService
 */
public interface B2BApproverService<T>
{

	/**
	 * Gets the approvers assigned to principal(customer) directly or as approver groups including its parent unit and
	 * all the units assigned to its parent up to the root unit.
	 *
	 * @param principal
	 *           the principal (customer)
	 * @return A collection of approvers
	 */
	List<T> getAllApprovers(final T principal);

	/**
	 * Checks if the principal is a member of the user group.
	 *
	 *
	 * @param principal
	 *           the principal
	 * @param userGroupUid
	 *           the uid of the user group to check if the principal is a memeber of.
	 * @return true, if is member of
	 * @deprecated Since 4.4. User
	 *             {@link de.hybris.platform.servicelayer.user.impl.DefaultUserService#isMemberOfGroup(de.hybris.platform.core.model.user.UserGroupModel, de.hybris.platform.core.model.user.UserGroupModel)}
	 */
	@Deprecated
	boolean isMemberOf(final PrincipalGroupModel principal, final String userGroupUid);

	/**
	 * Gets the list of account manager approvers.
	 *
	 *
	 * @param principal
	 *           the principal
	 * @return the approvers
	 */
	List<UserModel> getAccountManagerApprovers(PrincipalModel principal);

	/**
	 *
	 * Gets all the active approvers for a given customer
	 *
	 * @param principal
	 * @return list of approvers
	 */
	List<T> getAllActiveApprovers(B2BCustomerModel principal);


	/**
	 * Add an approver for a given user and return the updated {@link B2BCustomerModel} object updated with approver
	 * details
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param approver
	 *           A unique identifier for {@link B2BCustomerModel} representing a approver
	 * @return Updated {@link B2BCustomerModel} object updated with approvers
	 * @throws {@link
	 *            IllegalArgumentException} if the approver is not in the approver group
	 */
	B2BCustomerModel addApproverToCustomer(String user, String approver);

	/**
	 * Remove an approver for a given user and return the updated {@link B2BCustomerModel} object updated with approver
	 * details
	 *
	 * @param user
	 *           A unique identifier for {@link B2BCustomerModel} representing a user
	 * @param approver
	 *           A unique identifier for {@link B2BCustomerModel} representing a approver
	 * @return Updated {@link B2BCustomerModel} object removed with approver
	 */
	B2BCustomerModel removeApproverFromCustomer(String user, String approver);

	/**
	 * Add approver to given unit
	 *
	 * @param unitId
	 *           A unique identifier for {@link B2BUnitModel}
	 * @param approverId
	 *           A unique identifier for {@link B2BCustomerModel} who is a approver
	 * @return Updated {@link B2BCustomerModel} object after adding approver to customer.
	 */
	B2BCustomerModel addApproverToUnit(String unitId, String approverId);

	/**
	 * If an approver is a member of the B2BUnit remove b2bapprovergroup role, if the approver is a member of the current
	 * branch of units the approver will be removed from {@link de.hybris.platform.b2b.model.B2BUnitModel#getApprovers()}
	 * relationship
	 *
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param approverUid
	 *           A unique identifier of {@link B2BCustomerModel}
	 * @return An approver who was removed from a {@link de.hybris.platform.b2b.model.B2BUnitModel#getApprovers()}
	 */
	B2BCustomerModel removeApproverFromUnit(String unitUid, String approverUid);

	/**
	 * Gets list of {@link SearchPageData} {@link B2BCustomerModel} provided with required pagination parameters with
	 * {@link PageableData}
	 *
	 * @param pageableData
	 *           Pagination information
	 * @param unitUid
	 *           A unique identifier of {@link B2BUnitModel}
	 * @param userGroupUid
	 *           A unique identifier of {@link B2BUserGroupModel}
	 * @return Collection of paginated {@link B2BCostCenterModel} objects
	 */
	SearchPageData<B2BCustomerModel> findPagedApproversForUnitByGroupMembership(PageableData pageableData, String unitUid,
			String... userGroupUid);

}
