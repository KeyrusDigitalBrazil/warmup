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
package de.hybris.platform.sap.orderexchange.constants;

/**
 * Constants for Data Hub Inbound
 */
public class DataHubInboundConstants
{
	private DataHubInboundConstants() {
		throw new IllegalStateException("Utility class");
	}

	@SuppressWarnings("javadoc")
	public static final String CODE = "code";

	@SuppressWarnings("javadoc")
	public static final String IGNORE = "<ignore>";

	@SuppressWarnings("javadoc")
	public static final String DATA_HUB_ORDER_IMPORT = "dataHubOrderImport";

	@SuppressWarnings("javadoc")
	public static final String CONSIGNMENT_CREATION_EVENTNAME_PREFIX = "ConsignmentCreationEvent_";
	
	@SuppressWarnings("javadoc")
	public static final String GOODS_ISSUE_EVENTNAME_PREFIX = "GoodsIssueEvent_";

	@SuppressWarnings("javadoc")
	public static final String ERP_ORDER_CONFIRMATION_EVENT = "ERPOrderConfirmationEvent_";

	@SuppressWarnings("javadoc")
	public static final String IDOC_DATE_FORMAT = "yyyyMMdd";

	@SuppressWarnings("javadoc")
	public static final String DATE_NOT_SET = "00000000";

	@SuppressWarnings("javadoc")
	public static final String POUND_SIGN = "#";
	
	@SuppressWarnings("javadoc")
	public static final String UNDERSCORE = "_";
}
