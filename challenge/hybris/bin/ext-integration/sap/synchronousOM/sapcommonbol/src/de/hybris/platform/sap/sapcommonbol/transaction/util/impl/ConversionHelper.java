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
package de.hybris.platform.sap.sapcommonbol.transaction.util.impl;
import de.hybris.platform.sap.core.common.util.LocaleUtil;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Helper class for the data conversion for the Jco calls. <br>
 *
 */
public class ConversionHelper
{

	private static final String DATE_STRING_FORMAT_STR = "yyyyMMdd";
	

	/**
	 * Time zone which is used as default.
	 */
	 public static final String DEFAULT_TIMEZONE = "GMT";
	
	private ConversionHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * ThreadLocal SimpleDateFormat "yyyyMMdd".<br>
	 * SimpleDateFormat is not thread safe and expensive to construct. By Wrapping into a ThreadLocal object we avoid
	 * synchronisation issues and still get a decent performance, as the object is only created once per thread.
	 */
	private static final ThreadLocal<SimpleDateFormat> TL_DATE_STRING_FORMAT = new ThreadLocal<SimpleDateFormat>()
	{
		@Override
		protected SimpleDateFormat initialValue()
		{
			return new SimpleDateFormat(DATE_STRING_FORMAT_STR);
		}
	};

	private static final ThreadLocal<ParsePosition> TL_UNUSED = new ThreadLocal<ParsePosition>()
	{
		@Override
		protected ParsePosition initialValue()
		{
			return new ParsePosition(0);
		}

	};

	/**
	 * Converts a date string in format yyyyMMdd to a date.
	 *
	 * @param datsAsString
	 *           date string in format yyyyMMdd
	 * @return date
	 */
	public static Date convertDateStringToDate(final String datsAsString)
	{
		final ParsePosition pos = TL_UNUSED.get();
		// reset
		pos.setIndex(0);
		return TL_DATE_STRING_FORMAT.get().parse(datsAsString, pos);
	}

	/**
	 * Converts a date into a date string in format yyyyMMdd .
	 *
	 * @param date
	 *           in date string format
	 * @return date string in format yyyyMMdd
	 */
	public static String convertDateToDateString(final Date date)
	{
		return TL_DATE_STRING_FORMAT.get().format(date);
	}

	/**
	 * Converts a date into a localized date string (which is formatted according to the session locale)
	 *
	 * @param date
	 *           Date in date format
	 * @return localised date string
	 */
	public static String convertDateToLocalizedString(final Date date)
	{
		final SimpleDateFormat formatter = ConversionTools.getSDF(LocaleUtil.getLocale());
		return formatter.format(date);
	}

	/**
	 * Converts a BigDecimal to a String using the Locale defined in LocaleUtil. The fraction length is kept from the
	 * BigDecimal.
	 *
	 * @param bd
	 *           Big decimal
	 * @return String like "0.00"
	 */
	public static String convertBigDecimalToString(final BigDecimal bd)
	{
		if (bd == null)
		{
			return null;
		}
		final NumberFormat format = NumberFormat.getNumberInstance(LocaleUtil.getLocale());
		format.setMinimumFractionDigits(bd.scale());
		return format.format(bd);
	}

	/**
	 * Adjusts the BigDecimal from JCo value to the correct customized one.<br>
	 *
	 * @param value
	 *           BigDecimal from JCO
	 * @param decimal
	 *           customized number of decimals
	 * @return corrected BigDecimal
	 */
	public static BigDecimal adjustCurrencyDecimalPoint(final BigDecimal value, final int decimal)
	{
		final long withoutDecimalPoint = value.unscaledValue().longValue();
		return BigDecimal.valueOf(withoutDecimalPoint, decimal);
	}

}
