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
 * The Class B2BDayRange.
 * 
 * 
 */
public class DayRange implements TimeRange
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DayRange.class.getName());

	/**
	 * @see TimeRange#getEndOfRange(Calendar)
	 */
	public Calendar getEndOfRange(final Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 11);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		return calendar;
	}

	/**
	 * @see TimeRange#getStartOfRange(Calendar)
	 */
	public Calendar getStartOfRange(final Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
		return calendar;
	}
}
