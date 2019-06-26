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

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


/**
 * Object containing queues of recently viewed products and categories *
 */
public class RecentViewedItemsCollection implements Serializable
{
	private static final long serialVersionUID = -487537274245572411L;

	protected final ArrayDeque<String> codes = new ArrayDeque<>();
	protected final int maxEntries;

	public RecentViewedItemsCollection(final int maxEntries)
	{
		this.maxEntries = maxEntries;
	}

	public synchronized void addCode(final String code)
	{
		if (code == null || code.isEmpty())
		{
			return;
		}

		// Latest viewed code must be move back to first place.
		this.codes.remove(code);
		this.codes.add(code);

		while (this.codes.size() > this.maxEntries)
		{
			this.codes.poll();
		}
	}

	public synchronized List<String> getCodes()
	{
		return new ArrayList<>(codes);
	}

}
