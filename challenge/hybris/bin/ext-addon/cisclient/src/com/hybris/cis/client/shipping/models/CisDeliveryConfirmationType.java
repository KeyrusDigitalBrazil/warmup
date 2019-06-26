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
package com.hybris.cis.client.shipping.models;

/**
 * The Enum CisDeliveryConfirmationType.
 * 
 */
public enum CisDeliveryConfirmationType
{
	/**
	 * Only the addressee can sign the delivery confirmation.
	 */
	DIRECT,

	/**
	 * Less strict than DIRECT, someone else can sign the package.
	 */
	INDIRECT,

	/**
	 * FedEx Web Services selects the appropriate signature option for your shipping service.
	 */
	SERVICE_DEFAULT;

	/**
	 * Value.
	 * 
	 * @return the name
	 */
	public String value()
	{
		return this.name();
	}

	/**
	 * From value.
	 * 
	 * @param value the String value
	 * @return the cis delivery confirmation type
	 */
	public static CisDeliveryConfirmationType fromValue(final String value)
	{
		return valueOf(value);
	}
}
