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
package de.hybris.platform.b2bacceleratorfacades.order;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderHistoryEntryData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.workflow.enums.WorkflowActionType;

import java.util.List;


/**
 * Interface responsible for B2B order services.
 *
 */
public interface B2BOrderFacade extends OrderFacade
{
	/**
	 * Gets the schedule Cart Data for replenishment given a code.
	 *
	 * @param code
	 *           unique job identifier
	 * @param user
	 *           a customer assigned to the cart
	 * @return {@link ScheduledCartData for given code and user}
	 */
	ScheduledCartData getReplenishmentOrderDetailsForCode(String code, String user);

	/**
	 * Retrieves replenishment cron jobs associated to the session user.
	 *
	 * @return the replenishment order history.
	 */
	List<ScheduledCartData> getReplenishmentHistory();

	/**
	 * Retrieves replenishment cron jobs associated to the session user with paging support
	 *
	 * @param pageableData
	 *           the pagination information ({@link PageableData})
	 * @return the replenishment order history.
	 */
	SearchPageData<ScheduledCartData> getPagedReplenishmentHistory(PageableData pageableData);


	/**
	 * Stops the order replenishment process.
	 *
	 * @param jobCode
	 *           unique code for the replenishment cron job.
	 * @param user
	 *           customer assigned to the cart
	 */
	void cancelReplenishment(String jobCode, String user);

	/**
	 * Retrieves all the scheduled order for a given jobCode.
	 *
	 * @param jobCode
	 *           a cron job code
	 * @param user
	 *           a customer assigned to the cart
	 * @return orders associated to a schedule job.
	 */
	List<? extends OrderHistoryData> getReplenishmentOrderHistory(String jobCode, String user);

	/**
	 * Retrieves the order list for approval dashboard
	 *
	 * @return all orders pending approval
	 */
	List<B2BOrderApprovalData> getOrdersForApproval();

	/**
	 * Retrieves the order list for approval with configurable parameters.
	 *
	 * @param actionTypes
	 *           the action types
	 * @param pageableData
	 *           the pagination information
	 *
	 * @return all orders pending approval
	 */
	SearchPageData<B2BOrderApprovalData> getPagedOrdersForApproval(WorkflowActionType[] actionTypes, PageableData pageableData);

	/**
	 * Retrieves the approval details of a specific order code
	 *
	 * @param code
	 *           the order code.
	 *
	 * @return the B2B order approval data
	 */
	B2BOrderApprovalData getOrderApprovalDetailsForCode(String code);

	/**
	 * Sets the order approval decision to the workflow.
	 *
	 * @param b2bOrderApprovalData
	 *           the approval comments and decision action.
	 *
	 * @return the B2B order approval data
	 */
	B2BOrderApprovalData setOrderApprovalDecision(B2BOrderApprovalData b2bOrderApprovalData);

	/**
	 * Retrieves all the scheduled order for a given jobCode with configurable parameters.
	 *
	 * @param jobCode
	 *           unique code for the replenishment cron job.
	 * @param pageableData
	 *           the pagination information
	 *
	 * @return a list of scheduled orders
	 */
	SearchPageData<? extends OrderHistoryData> getPagedReplenishmentOrderHistory(String jobCode, PageableData pageableData);

	/**
	 * Retrieves the history entry data of an order.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 *
	 * @return a list of history entries.
	 */
	List<B2BOrderHistoryEntryData> getOrderHistoryEntryData(String orderCode);

	/**
	 * @deprecated Since 6.3. Use quote functionality from commerce instead. ({@link QuoteFacade}).<br/>
	 *             Creates an order from a rejected quote.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 */
	@Deprecated
	void createAndSetNewOrderFromRejectedQuote(String orderCode);

	/**
	 * @deprecated Since 6.3. Use quote functionality from commerce instead. ({@link QuoteFacade}).<br/>
	 *             Creates an order from a negotiate quote.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 * @param comment
	 *           comment string.
	 */
	@Deprecated
	void createAndSetNewOrderFromNegotiateQuote(String orderCode, String comment);

	/**
	 * @deprecated Since 6.3. Use quote functionality from commerce instead. ({@link QuoteFacade}).<br/>
	 *             Creates an order from an approved quote.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 * @param comment
	 *           comment string.
	 */
	@Deprecated
	void createAndSetNewOrderFromApprovedQuote(String orderCode, String comment);

	/**
	 * Cancels an order.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 * @param comment
	 *           comment string.
	 */
	void cancelOrder(String orderCode, String comment);

	/**
	 * Add a comment to an order.
	 *
	 * @param orderCode
	 *           the unique code of an order.
	 * @param comment
	 *           comment string.
	 */
	void addAdditionalComment(String orderCode, String comment);
}
