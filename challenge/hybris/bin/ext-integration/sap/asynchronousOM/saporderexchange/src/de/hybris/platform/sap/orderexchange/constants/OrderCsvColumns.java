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
 * Constants for Order CSV Columns
 */
public class OrderCsvColumns
{
	private OrderCsvColumns() {
		throw new IllegalStateException("Utility class");
	}

	//	order
	@SuppressWarnings("javadoc")
	public static final String DATE = "date";

	@SuppressWarnings("javadoc")
	public static final String DELIVERY_MODE = "deliveryMode";

	@SuppressWarnings("javadoc")
	public static final String PAYMENT_MODE = "paymentMode";

	@SuppressWarnings("javadoc")
	public static final String ORDER_CURRENCY_ISO_CODE = "orderCurrencyIsoCode";
	
	@SuppressWarnings("javadoc")
	public static final String LOGICAL_SYSTEM = "logicalSystem";
	
	@SuppressWarnings("javadoc")
	public static final String SALES_ORGANIZATION = "salesOrganization";
	
	@SuppressWarnings("javadoc")
	public static final String DISTRIBUTION_CHANNEL = "distributionChannel";
	
	@SuppressWarnings("javadoc")
	public static final String DIVISION = "division";


	//	general columns
	@SuppressWarnings("javadoc")
	public static final String ORDER_ID = "orderId";

	@SuppressWarnings("javadoc")
	public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";

	@SuppressWarnings("javadoc")
	public static final String BASE_STORE = "baseStore";

	@SuppressWarnings("javadoc")
	public static final String CHANNEL = "channel";
	
	
}
