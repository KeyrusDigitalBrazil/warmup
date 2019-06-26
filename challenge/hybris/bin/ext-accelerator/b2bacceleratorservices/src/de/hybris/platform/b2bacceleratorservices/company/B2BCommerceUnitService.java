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
package de.hybris.platform.b2bacceleratorservices.company;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * A service for unit management within b2b commerce.
 *
 * Interface kept for backwards compatibility reasons.
 *
 * @deprecated Since 6.0. Use {@link de.hybris.platform.b2b.company.B2BCommerceUnitService} instead.
 */
@Deprecated
public interface B2BCommerceUnitService extends de.hybris.platform.b2b.company.B2BCommerceUnitService
{
	/**
	 * Gets a customer for a given uid
	 *
	 * @param uid
	 *           A unique identifier for {@link B2BCustomerModel}
	 * @return A {@link B2BCustomerModel} for the given uid
	 */
	<T extends B2BCustomerModel> T getCustomerForUid(String uid);

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
