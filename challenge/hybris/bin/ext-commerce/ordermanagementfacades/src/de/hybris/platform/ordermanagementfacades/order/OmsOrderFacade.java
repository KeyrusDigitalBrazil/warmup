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
package de.hybris.platform.ordermanagementfacades.order;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.fraud.data.FraudReportData;
import de.hybris.platform.ordermanagementfacades.order.cancel.OrderCancelRecordEntryData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;

import java.util.List;
import java.util.Set;


/**
 * OrderManagement facade exposing operations on {@link de.hybris.platform.core.model.order.OrderModel}
 */
public interface OmsOrderFacade
{
	/**
	 * API to get all orders in the system
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @return SearchPageData that contains a list of orders
	 */
	SearchPageData<OrderData> getOrders(PageableData pageableData);

	/**
	 * API to get an order by it's code
	 *
	 * @param orderCode
	 * 		the order's code
	 * @return the order
	 */
	OrderData getOrderForCode(String orderCode);

	/**
	 * API to get all orders in the system that have certain status(es)
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @param orderStatusSet
	 * 		set of order status(s) in which we want to get list of orders for
	 * @return SearchPageData that contains a list of orders that complies with passed order status(es)
	 */
	SearchPageData<OrderData> getOrdersByStatuses(PageableData pageableData, Set<OrderStatus> orderStatusSet);

	/**
	 * API to get all order statuses
	 *
	 * @return a list of {@link OrderStatus}
	 */
	List<OrderStatus> getOrderStatuses();

	/**
	 * API to get orderEntries for the given {@link de.hybris.platform.core.model.order.OrderModel#CODE}
	 *
	 * @param orderCode
	 * 		the order's code
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @return SearchPageData that contains a list of the orderEntries for the given order
	 */
	SearchPageData<OrderEntryData> getOrderEntriesForOrderCode(String orderCode, PageableData pageableData);

	/**
	 * API to get an orderEntry by it's entryNumber and its order's code
	 *
	 * @param orderCode
	 * 		the order's code
	 * @param entryNumber
	 * 		the order entry's number
	 * @return the order entry
	 */
	OrderEntryData getOrderEntryForOrderCodeAndEntryNumber(String orderCode, Integer entryNumber);

	/**
	 * API to get an order's fraud reports
	 *
	 * @param orderCode
	 * 		code of the order for which to get the fraud reports
	 * @return a list of fraud reports
	 */
	List<FraudReportData> getOrderFraudReports(String orderCode);

	/**
	 * API to approve a potentially fraudulent order
	 *
	 * @param orderCode
	 * 		code of the order for which to approve the fraud check
	 */
	void approvePotentiallyFraudulentOrder(String orderCode);

	/**
	 * API to reject a potentially fraudulent order
	 *
	 * @param orderCode
	 * 		code of the order for which to reject the fraud check
	 */
	void rejectPotentiallyFraudulentOrder(String orderCode);

	/**
	 * API to get all order cancel reasons
	 *
	 * @return a list of {@link CancelReason}
	 */
	List<CancelReason> getCancelReasons();

	/**
	 * API to submit an {@link OrderModel} in the system
	 *
	 * @param orderRequestData
	 * 		the {@link OrderRequestData} to submit an {@link OrderModel}
	 * @return the submitted {@link OrderData}
	 */
	OrderData submitOrder(OrderRequestData orderRequestData);

	/**
	 * API to request a order cancel{@link de.hybris.platform.ordercancel.OrderCancelRequest}
	 *
	 * @param orderCancelRequestData
	 * 		the {@link OrderCancelRequestData} to create {@link OrderCancelRequestData}
	 * @return {@link OrderCancelRecordEntryData} representing the cancel request that was created
	 */
	OrderCancelRecordEntryData createRequestOrderCancel(OrderCancelRequestData orderCancelRequestData);

	/**
	 * API to manually release void payment in waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleasePaymentVoid(String orderCode);

	/**
	 * API to manually release void tax in waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleaseTaxVoid(String orderCode);

	/**
	 * API to manually commit tax and release {@link OrderModel} from waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleaseTaxCommit(String orderCode);

	/**
	 * API to manually requote tax and release {@link OrderModel} from waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleaseTaxRequote(String orderCode);

	/**
	 * API to manually reauth payment and release {@link OrderModel} from waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleasePaymentReauth(String orderCode);

	/**
	 * API to manually commit delivery cost taxes and release {@link OrderModel} from waiting step.
	 *
	 * @param orderCode
	 * 		the {@link OrderModel#CODE}
	 */
	void manuallyReleaseDeliveryCostCommit(String orderCode);
}
