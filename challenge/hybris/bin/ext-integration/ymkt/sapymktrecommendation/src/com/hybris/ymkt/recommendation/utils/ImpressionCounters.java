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
package com.hybris.ymkt.recommendation.utils;

/**
 *
 */
public class ImpressionCounters
{
	private int impressionCount = 0;
	private int itemCount = 0;

	public void addToImpressionCount(final int newCount)
	{
		this.impressionCount += newCount;
	}

	public void addToItemCount(final int newCount)
	{
		this.itemCount += newCount;
	}

	public int getImpressionCount()
	{
		return impressionCount;
	}

	public int getItemCount()
	{
		return itemCount;
	}
}
