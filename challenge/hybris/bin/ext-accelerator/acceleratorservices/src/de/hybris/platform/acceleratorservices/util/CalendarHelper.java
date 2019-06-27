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
package de.hybris.platform.acceleratorservices.util;


import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * Static class containing helper methods for dates and calendar operations.
 */
public final class CalendarHelper
{
	private CalendarHelper()
	{
		// Empty private constructor
	}

	/**
	 * Method allowing to parse date from String parameters representing a month and a year
	 *
	 * @param month to parse
	 * @param year to parse
	 * @return Calendar with the proper date or null for incorrect parameters
	 */
	public static Calendar parseDate(final String month, final String year)
	{
		if (StringUtils.isNotBlank(month) && StringUtils.isNotBlank(year))
		{
			final int yearInt = NumberUtils.toInt(year, -1);
			final int monthInt = NumberUtils.toInt(month, -1);

			if (yearInt != -1 && monthInt != -1)
			{
				final Calendar date = getCalendarResetTime();
				date.set(Calendar.YEAR, yearInt);
				date.set(Calendar.MONTH, monthInt - 1);
				date.set(Calendar.DAY_OF_MONTH, 1);
				return date;
			}
		}
		return null;
	}

	/**
	 * Method for obtaining a calendar instance with time values set to 0.
	 *
	 * @return a Calendar object.
	 */
	public static Calendar getCalendarResetTime()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

}
