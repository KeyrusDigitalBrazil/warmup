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
package de.hybris.platform.b2b.util.impl;

import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.util.DateRangeFactory;
import de.hybris.platform.b2b.util.DayRange;
import de.hybris.platform.b2b.util.MonthRange;
import de.hybris.platform.b2b.util.QuarterRange;
import de.hybris.platform.b2b.util.TimeRange;
import de.hybris.platform.b2b.util.WeekRange;
import de.hybris.platform.b2b.util.YearRange;
import de.hybris.platform.util.StandardDateRange;

import java.util.Calendar;



public class DefaultDateRangeFactory implements DateRangeFactory
{
	public StandardDateRange createDateRange(final B2BPeriodRange range)
	{
		TimeRange timeRange = new DayRange();
		switch (range)
		{
			case DAY:
				timeRange = new DayRange();
				break;
			case WEEK:
				timeRange = new WeekRange();
				break;
			case MONTH:
				timeRange = new MonthRange();
				break;
			case QUARTER:
				timeRange = new QuarterRange();
				break;
			case YEAR:
				timeRange = new YearRange();
				break;
		}
		return new StandardDateRange(timeRange.getStartOfRange(Calendar.getInstance()).getTime(), timeRange.getEndOfRange(
				Calendar.getInstance()).getTime());
	}
}
