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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import org.apache.commons.lang.StringUtils;


enum CPSIntervalType
{
	UNCONSTRAINED_DOMAIN("0"), SINGLE_VALUE("1"), HALF_OPEN_RIGHT_INTERVAL("2"), CLOSED_INTERVAL("3"), HALF_OPEN_LEFT_INTERVAL(
			"4"), OPEN_INTERVAL("5"), INFINITY_TO_HIGH_OPEN_INTERVAL("6"), INFINITY_TO_HIGH_CLOSED_INTERVAL(
					"7"), LOW_TO_INFINITY_OPEN_INTERVAL("8"), LOW_TO_INFINITY_CLOSED_INTERVAL("9");

	private String type;

	CPSIntervalType(final String type)
	{
		this.type = type;
	}

	public static CPSIntervalType fromString(final String intervalType)
	{
		for (final CPSIntervalType interval : CPSIntervalType.values())
		{
			if (interval.type.equals(intervalType))
			{
				return interval;
			}
		}

		return null;
	}

	public static boolean isInterval(final String intervalType)
	{
		boolean isInterval = true;

		if (StringUtils.isEmpty(intervalType))
		{
			isInterval = false;
		}
		else if (!intervalType.matches("\\d"))
		{
			isInterval = false;
		}
		else
		{
			final int intervalValue = Integer.parseInt(intervalType);
			if (2 > intervalValue || 9 < intervalValue)
			{
				isInterval = false;
			}
		}

		return isInterval;
	}
}
