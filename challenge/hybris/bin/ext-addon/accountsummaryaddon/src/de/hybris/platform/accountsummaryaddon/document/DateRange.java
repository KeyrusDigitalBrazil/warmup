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
package de.hybris.platform.accountsummaryaddon.document;

import java.util.Date;

public class DateRange implements Range
{

	private final Date minDateRange;
	private final Date maxDateRange;

	public DateRange(final Date minDateRange, final Date maxDateRange)
	{
		this.minDateRange = minDateRange;
		this.maxDateRange = maxDateRange;
	}


	@Override
	public Date getMinBoundary()
	{
		return this.minDateRange;
	}


	@Override
	public Date getMaxBoundary()
	{
		return this.maxDateRange;
	}
}
