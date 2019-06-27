/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementfacades.returns;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.returns.data.CancelReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestModificationData;

import java.util.List;
import java.util.Set;


/**
 * API for return facade
 */
public interface OmsReturnFacade
{
	/**
	 * API to get all returns in the system
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page
	 * 		in addition the sorting info
	 * @return SearchPageData that contains a list of returns
	 */
	SearchPageData<ReturnRequestData> getReturns(PageableData pageableData);

	/**
	 * Returns the paged {@link ReturnRequestData} for the current user.
	 *
	 * @param pageableData
	 * 		paging information
	 * @param returnStatuses
	 * 		the list of {@link ReturnStatus}. If null or empty, all statuses are included.
	 * @return The {@link ReturnRequestData} for the current user.
	 */
	SearchPageData<ReturnRequestData> getPagedReturnRequestsByCurrentUser(PageableData pageableData,
			ReturnStatus... returnStatuses);

	/**
	 * API to get all returns in the system that have certain status(s)
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page
	 * 		in addition the sorting info
	 * @param returnStatusSet
	 * 		set of return status(s) in which we want to get list of returns for.
	 * @return SearchPageData that contains a list of returns that complies with passed return status(es)
	 */
	SearchPageData<ReturnRequestData> getReturnsByStatuses(PageableData pageableData, Set<ReturnStatus> returnStatusSet);

	/**
	 * API to get all return statuses
	 *
	 * @return a list of {@link de.hybris.platform.basecommerce.enums.ReturnStatus}
	 */
	List<ReturnStatus> getReturnStatuses();

	/**
	 * API to get returnEntries for the given {@link de.hybris.platform.returns.model.ReturnRequestModel#CODE}
	 *
	 * @param code
	 * 		the return's code
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @return SearchPageData that contains a list of the returnEntries for the given order
	 */
	SearchPageData<ReturnEntryData> getReturnEntriesForReturnCode(String code, PageableData pageableData);

	/**
	 * API to create a {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 *
	 * @param returnRequestData
	 * 		the {@link ReturnRequestData} to create {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 * @return the {@link ReturnRequestData} converted from the newly created {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 */
	ReturnRequestData createReturnRequest(ReturnRequestData returnRequestData);

	/**
	 * API to get a {@link ReturnRequestData} by its code
	 *
	 * @param code
	 * 		the returnRequest's code
	 * @return the returnRequest
	 */
	ReturnRequestData getReturnForReturnCode(String code);

	/**
	 * API to get all return cancel reasons
	 *
	 * @return a list of {@link CancelReason}
	 */
	List<CancelReason> getCancelReasons();

	/**
	 * API to get all return refund reasons
	 *
	 * @return a list of {@link RefundReason}
	 */
	List<RefundReason> getRefundReasons();

	/**
	 * API to get all return actions
	 *
	 * @return a list of {@link ReturnAction}
	 */
	List<ReturnAction> getReturnActions();

	/**
	 * API to check if the  {@link ReturnRequestData} contains a complete order return.
	 *
	 * @param orderModel
	 * 		the {@link OrderModel} related to the return request
	 * @param returnRequestData
	 * 		the return form result {@link ReturnRequestData}
	 * @return true if it's a complete return.
	 */
	Boolean isCompleteReturn(OrderModel orderModel, ReturnRequestData returnRequestData);

	/**
	 * API to approve the {@link de.hybris.platform.returns.model.ReturnRequestModel} by its code
	 *
	 * @param code
	 * 		the code for the {@link de.hybris.platform.returns.model.ReturnRequestModel} to be approved
	 */
	void approveReturnRequest(String code);

	/**
	 * API to update the {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 *
	 * @param code
	 * 		the return request's code
	 * @param returnRequestModificationData
	 * 		{@link ReturnRequestModificationData}
	 * @return {@link ReturnRequestData}
	 */
	ReturnRequestData updateReturnRequest(String code, ReturnRequestModificationData returnRequestModificationData);

	/**
	 * API to cancel a {@link de.hybris.platform.returns.model.ReturnRequestModel}
	 *
	 * @param cancelReturnRequestData
	 * 		contains information about the cancellation of the return request.
	 */
	void cancelReturnRequest(CancelReturnRequestData cancelReturnRequestData);

	/**
	 * API to reverse payment manually for the returnRequest by its code
	 *
	 * @param code
	 * 		the code for the returnRequest
	 */
	void requestManualPaymentReversalForReturnRequest(String code);

	/**
	 * API to reverse tax manually for the returnRequest by its code
	 *
	 * @param code
	 * 		the code for the returnRequest
	 */
	void requestManualTaxReversalForReturnRequest(String code);

	/**
	 * Verifies if the delivery cost is refundable for the given order and return request.
	 *
	 * @param orderCode
	 * 		order code
	 * @param returnRequestRMA
	 * 		the return request RMA
	 * @return true if the delivery cost can be refunded
	 */
	boolean isDeliveryCostRefundable(String orderCode, String returnRequestRMA);
}
