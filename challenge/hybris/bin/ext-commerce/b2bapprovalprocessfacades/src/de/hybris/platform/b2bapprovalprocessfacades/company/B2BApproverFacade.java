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
package de.hybris.platform.b2bapprovalprocessfacades.company;

import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;


/**
 * The B2BApproverFacade manages approvals
 *
 * @since 6.0
 */
public interface B2BApproverFacade
{
	/**
	 * Gets a paged list of approvers. Approvers already assigned to the business unit with <param>unitUid</param> are
	 * marked as selected.
	 *
	 * @param pageableData
	 *           Pagination data
	 * @param unitUid
	 *           A unit id of the business unit from which to check selected approvers.
	 * @return A paged approver data.
	 */
	SearchPageData<CustomerData> getPagedApproversForUnit(PageableData pageableData, String unitUid);

	/**
	 * Adds an approver to a unit.
	 *
	 * @param unitUid
	 *           A unit to add an approver to
	 * @param approverUid
	 *           The approver to add to a unit's list of approvers
	 * @return An approver if added successfully otherwise null.
	 */
	B2BSelectionData addApproverToUnit(final String unitUid, final String approverUid);

	/**
	 * Removes an approver from a unit.
	 *
	 * @param unitUid
	 *           A business unit uid.
	 * @param approverUid
	 *           An approver uid.
	 * @return An approver
	 */
	B2BSelectionData removeApproverFromUnit(String unitUid, String approverUid);

	/**
	 * Returns the list of approvers for a customers.
	 *
	 * @param pageableData
	 *           Pagination Data
	 * @param customerUid
	 *           the uid of the customer
	 * @return Get Paginated list found approvers
	 */
	SearchPageData<CustomerData> getPagedApproversForCustomer(PageableData pageableData, String customerUid);

	/**
	 * Adds an approver for a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param approverUid
	 *           the uid of the approver
	 * @return Returns the {@link B2BSelectionData}
	 */
	B2BSelectionData addApproverForCustomer(String customerUid, String approverUid);

	/**
	 * Removes an approver from a customer.
	 *
	 * @param customerUid
	 *           the uid of the customer
	 * @param approverUid
	 *           the uid of the approver
	 * @return Returns the {@link B2BSelectionData}
	 */
	B2BSelectionData removeApproverFromCustomer(String customerUid, String approverUid);
}
