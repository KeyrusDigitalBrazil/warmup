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

public enum CisWeightUnitsType
{
	/**
	 * Kilograms.
	 */
	KG,
	/**
	 * Pounds.
	 */
	LB,
	/**
	 * Ounces.
	 */
	OZ;

	public String value()
	{
		return this.name();
	}

	public static CisWeightUnitsType fromValue(final String v)
	{
		for (final CisWeightUnitsType c : CisWeightUnitsType.values())
		{
			if (c.toString().equalsIgnoreCase(v))
			{
				return c;
			}
		}
		throw new IllegalArgumentException("No enum value found for: " + v);
	}

}
