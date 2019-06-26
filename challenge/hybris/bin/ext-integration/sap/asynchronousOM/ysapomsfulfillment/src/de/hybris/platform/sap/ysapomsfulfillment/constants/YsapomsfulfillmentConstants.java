/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.ysapomsfulfillment.constants;

/**
 * Constants used in ysapomsfulfillment extension, e.g. process name, event name, ...
 */
@SuppressWarnings("deprecation")
public final class YsapomsfulfillmentConstants extends GeneratedYsapomsfulfillmentConstants
{
	public static final String ACTION_COMPLETION_EVENT_NAME = "ActionCompletionEvent";
	public static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	public static final String ORDER_PROCESS_NAME = "sap-oms-order-process";
	public static final String CONSIGNMENT_SUBPROCESS_NAME = "sap-oms-consignment-process";
	public static final String CONSIGNMENT_PICKUP = "ConsignmentPickup";
	
	public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";
	public static final int DEFAULT_MAX_RETRIES = 10;
	public static final int DEFAULT_RETRY_DELAY = 60 * 1000; // 60,000 milliseconds = 1 minute 
	
	public static final String SAP_CONS = "_sap_cons_";
	public static final String ORDER_MANAGEMENT = "_ordermanagement";
	public static final String UNDERSCORE = "_";
	
	public static final String MISSING_SALES_ORG = "MISSING_SALES_ORG";
	public static final String MISSING_LOG_SYS = "MISSING_LOGICAL_SYSTEM";
	public static final String CONSIGNMENT_PROCESS_ENDED = "consignmentProcessEnded";
	public static final String CANCEL_ORDER = "cancelOrder";
	
	public static final String WAIT_FOR_CONSIGNMENT_ACTION = "waitForConsignmentAction";
	public static final String WAIT_FOR_GOODS_ISSUE = "waitForGoodsIssue";
	public static final String WAIT_FOR_ERP_CONFIRMATION = "waitForERPConfirmation";
	public static final String WAIT_FOR_ORDER_ACTION = "waitForOrderAction";
	public static final String CANCEL_ORDER_ACTION = "cancelOrderAction";
	public static final String CANCEL_CONSIGNMENT_ACTION = "cancelConsignmentAction";

	
	
}
