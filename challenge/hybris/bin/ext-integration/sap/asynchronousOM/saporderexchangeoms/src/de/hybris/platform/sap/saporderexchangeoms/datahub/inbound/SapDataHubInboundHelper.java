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
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound;

/**
 * OMS Data Hub Inbound Helper for Delivery and PGI related notifications
 */
public interface SapDataHubInboundHelper
{
	/**
	 * @param orderCode
	 * @param entryNumber
	 */
	void processDeliveryNotification(String orderCode, String entryNumber);

	/**
	 * @param orderCode
	 * @param entryNumber
	 * @param goodsIssueDate
	 */
	void processGoodsIssueNotification(String orderCode, String entryNumber, String quantity, String goodsIssueDate);

	/**
	 * @param deliveryInfo
	 * @return Goods issue date
	 */
	String determineGoodsIssueDate(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return EntryNumber
	 */
	String determineEntryNumber(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return Quantity
	 */
	String determineQuantity(String deliveryInfo);

}
