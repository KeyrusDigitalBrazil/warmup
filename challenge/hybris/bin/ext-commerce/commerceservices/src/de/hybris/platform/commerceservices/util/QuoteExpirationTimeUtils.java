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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;


/**
 * Utility class for determining quote expiration time and checking its validity. It also provides methods for getting
 * the configurable offer validity periods.
 */
public final class QuoteExpirationTimeUtils
{
	public static final int DEFAULT_OFFER_VALIDITY_PERIOD_IN_DAYS = 30;
	public static final int MIN_OFFER_VALIDITY_PERIOD_IN_DAYS = 1;

	private QuoteExpirationTimeUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	/**
	 * Gets the configured value for the quote default offer validity period in days or 30 if the property is not found.
	 *
	 * @return the quote default offer validity period in days
	 */
	public static int getDefaultOfferValidityPeriodDays()
	{
		return Config.getInt(CommerceServicesConstants.QUOTE_DEFAULT_OFFER_VALIDITY_PERIOD_IN_DAYS,
				DEFAULT_OFFER_VALIDITY_PERIOD_IN_DAYS);
	}

	/**
	 * Gets the configured value for the quote minimum offer validity period in days or 1 if the property is not found.
	 *
	 * @return the quote minimum offer validity period in days
	 */
	public static int getMinOfferValidityPeriodInDays()
	{
		return Config.getInt(CommerceServicesConstants.QUOTE_MIN_OFFER_VALIDITY_PERIOD_IN_DAYS, MIN_OFFER_VALIDITY_PERIOD_IN_DAYS);
	}

	/**
	 * Determines the expiration time for a quote by checking if the expiration time that is currently set for the quote
	 * is valid or not using {@link #isExpirationTimeValid(Date, Date)} method. If it is valid, it returns the expiration
	 * time as is, otherwise it returns an expiration time set to current date with normalized time plus the quote
	 * default offer validity period in days and with the time part set to end day.
	 *
	 * @param oldExpiryDate
	 *           the expiration date that is currently set for the quote
	 * @param today
	 *           the current date with normalized time
	 * @return the determined expiration time for the quote
	 */
	public static Date determineExpirationTime(final Date oldExpiryDate, final Date today)
	{
		if (isExpirationTimeValid(oldExpiryDate, today))
		{
			return oldExpiryDate;
		}
		return getEndOfDay(DateUtils.addDays(today, getDefaultOfferValidityPeriodDays()));
	}

	/**
	 * Sets the time part of the provided input date parameter to end of day (23:59:59).
	 *
	 * @param day
	 *           the date for which the time part to be set to end of day
	 * @return the updated date object with the time part set to end of day
	 */
	public static Date getEndOfDay(final Date day)
	{
		return DateUtils.addMilliseconds(DateUtils.ceiling(day, Calendar.DATE), -1);
	}

	/**
	 * Checks if the expiration date provided as input is valid or not. An expiration date is not valid if it is before the
	 * current date with normalized time plus the quote minimum offer validity period in days or it is not set.
	 * Otherwise, the expiration date is valid.
	 *
	 * @param expiryDate
	 *           the expiration date to check
	 * @param today
	 *           the current date with normalized time
	 * @return true if the expiration date is valid, otherwise false
	 */
	public static boolean isExpirationTimeValid(final Date expiryDate, final Date today)
	{
		if ((expiryDate == null) || expiryDate.before(DateUtils.addDays(today, getMinOfferValidityPeriodInDays())))
		{
			return false;
		}
		return true;
	}
}
