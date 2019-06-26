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



/**
 * <p>
 * The Interface provides Strategy methods (<a
 * href="http://en.wikipedia.org/wiki/Strategy_pattern">http://en.wikipedia.org/wiki/Strategy_pattern</a>) to calculate
 * border dates for time ranges. Any new Strategy have to be implemented using this interface. By default there are five
 * Strategies provided by B2B extension:
 * </p>
 * <ul>
 * <li>Day Strategy - {@link DayRange}
 * <li>Week Strategy - {@link WeekRange}
 * <li>Month Strategy - {@link MonthRange}
 * <li>Quarter Strategy - {@link QuarterRange}
 * <li>Year Strategy - {@link YearRange}
 * </ul>
 * <p>
 * Adding new Strategy is a four steps process.
 * </p>
 * <ul>
 * <li>creating class implementing this interface
 * <li>adding Spring configuration entry in <tt>resources/b2bcore-spring.xml</tt> like this:
 * 
 * <pre>
 *   &lt;bean id=&quot;de.hybris.platform.b2b.scope.YourRange&quot; name=&quot;YOUR_RANGE&quot; class=&quot;de.hybris.platform.b2b.scope.YourRange&quot; scope=&quot;singleton&quot; /&gt;
 * </pre>
 * 
 * <li>adding new value to Hybris <tt>Enumeration</tt> type called <code>B2BPeriodRange</code> in
 * <tt>resources/b2bcore-items.xml</tt> like this:
 * 
 * <pre>
 *   &lt;value code=&quot;YOUR_RANGE&quot; /&gt;
 * </pre>
 * 
 * <li>adding localized name of new <tt>Enumeration</tt> value in appropriate files stored in
 * <tt>resources/localization/</tt> directory to "Time ranges" section:
 * 
 * <pre>
 *  type.b2bperiodrange.your_range.name=Your range
 * </pre>
 * 
 * </ul>
 * <p>
 * The last step is very important, so please be careful and add it immediately after creating new <tt>Enumeration</tt>
 * value. Localized names have to be provided for all supported languages in your application.
 * </p>
 * 
 * 
 * 
 */
public interface TimeRange
{
	/**
	 * Gets the end of range. It is assumed that hour, minute and second in return <code>Calendar</code> object is set to
	 * 23:59:59.
	 * 
	 * @param calendar
	 *           object to determines current date/time, which is base date/time for calculations
	 * @return the end of range
	 */
	Calendar getEndOfRange(Calendar calendar);

	/**
	 * Gets the start of range. It is assumed that hour, minute and second in return <code>Calendar</code> object is set
	 * to 00:00:00.
	 * 
	 * @param calendar
	 *           object to determines current date and time, which is base date/time for calculations
	 * @return the start of range
	 */
	Calendar getStartOfRange(Calendar calendar);
}
