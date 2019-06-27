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
package de.hybris.platform.b2b.util;

import java.util.Calendar;

import org.apache.log4j.Logger;


/**
 * The Class B2BQuarterRange.
 *
 *
 */
public class QuarterRange implements TimeRange
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(QuarterRange.class.getName());

	/**
	 * @see TimeRange#getEndOfRange(Calendar)
	 */
	public Calendar getEndOfRange(final Calendar calendar)
	{
		final Integer[] borderMonths = getQuarterBorderMonths(calendar);
		calendar.set(calendar.get(Calendar.YEAR), borderMonths[1].intValue(), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		return calendar;
	}

	/**
	 * @see TimeRange#getStartOfRange(Calendar)
	 */
	public Calendar getStartOfRange(final Calendar calendar)
	{
		final Integer[] borderMonths = getQuarterBorderMonths(calendar);
		calendar.set(calendar.get(Calendar.YEAR), borderMonths[0].intValue(), calendar.getActualMinimum(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		return calendar;
	}

	/**
	 * Gets the quarter of year as integer.
	 *
	 * @param calendar
	 *           the calendar
	 * @return the quarter
	 */
	protected Integer[] getQuarterBorderMonths(final Calendar calendar)
	{
		final int month = calendar.get(Calendar.MONTH);
		Integer[] result = null;
		if (month >= Calendar.JANUARY && month <= Calendar.MARCH)
		{
			result = new Integer[]
			{ Integer.valueOf(Calendar.JANUARY), Integer.valueOf(Calendar.MARCH) };
		}
		else if (month >= Calendar.APRIL && month <= Calendar.JUNE)
		{
			result = new Integer[]
			{ Integer.valueOf(Calendar.APRIL), Integer.valueOf(Calendar.JUNE) };
		}
		else if (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)
		{
			result = new Integer[]
			{ Integer.valueOf(Calendar.JULY), Integer.valueOf(Calendar.SEPTEMBER) };
		}
		else if (month >= Calendar.OCTOBER && month <= Calendar.DECEMBER)
		{
			result = new Integer[]
			{ Integer.valueOf(Calendar.OCTOBER), Integer.valueOf(Calendar.DECEMBER) };
		}
		return result;
	}
}
