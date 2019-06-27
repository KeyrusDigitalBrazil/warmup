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
package com.hybris.cis.client.shared.models;


/**
 * Indicates the type of an address.
 */
public enum CisAddressType
{
	/** address where the order is shipped to (e.g. the consumer's home). */
	SHIP_TO,

	/** address where the order is shipped from (e.g. the warehouse). */
	SHIP_FROM,

	/** consumer's billing address. */
	BILL_TO,

	/**
	 * Place of business where the customer's order is accepted/approved, thereby becoming contractually obligated to
	 * make the sale.
	 */
	ADMIN_ORIGIN;
}
