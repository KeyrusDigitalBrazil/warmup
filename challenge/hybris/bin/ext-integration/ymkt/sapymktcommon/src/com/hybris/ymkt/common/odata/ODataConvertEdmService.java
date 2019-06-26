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
package com.hybris.ymkt.common.odata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.olingo.odata2.api.edm.EdmElement;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFacets;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.core.edm.EdmDateTime;
import org.apache.olingo.odata2.core.edm.EdmDateTimeOffset;
import org.apache.olingo.odata2.core.edm.EdmDecimal;
import org.apache.olingo.odata2.core.edm.EdmInt32;
import org.apache.olingo.odata2.core.edm.EdmString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Convert object value into well typed, well formated OData value.
 */
public class ODataConvertEdmService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataConvertEdmService.class);

	protected static final Integer MAX_VALUE = Integer.valueOf(Integer.MAX_VALUE);
	protected static final ZoneId UTC = ZoneId.of("UTC");

	/**
	 * Convert, align, adjust Java Object to the Olingo library input field requirements.<br>
	 * <ul>
	 * <li>Round/truncate number/date according to the EDM precision.</li>
	 * <li>Truncate String according to the max length.</li>
	 * <li>Convert String into proper Java Objects.</li>
	 * <li>etc.</li>
	 * </ul>
	 *
	 * @param value
	 *           object value
	 * @param property
	 *           {@link EdmTyped} of the OData value mapped field
	 * @return OData value
	 * @throws EdmException
	 *            {@link EdmException}
	 */
	public Object convertEdm(final Object value, final EdmTyped property) throws EdmException
	{
		final EdmElement element = (EdmElement) property;
		switch (element.getType().getName())
		{
			case "DateTime":
				return convertEdmDateTime(value, element);
			case "DateTimeOffset":
				return convertEdmDateTimeOffset(value, element);
			case "Decimal":
				return convertEdmDecimal(value, element);
			case "Int16":
			case "Int32":
				return convertEdmInt32(value, element);
			case "String":
				return convertEdmString(value, element);
			default:
		}
		// Override this method to intercept other types.
		// Check all child type of AbstractSimpleType in package org.apache.olingo.odata2.core.edm
		return value;
	}

	protected Object convertEdmDateTime(final Object value, final EdmElement element) throws EdmException
	{
		EdmDateTime.getInstance();
		if (value == null || //
				value instanceof Date || //
				value instanceof Calendar || //
				value instanceof Long)
		{
			return value;
		}
		if (value instanceof String)
		{
			final String s = (String) value;
			// Empty string is a null date
			if (s.isEmpty())
			{
				return null;
			}
			return this.convertEdmDateTime(s, element);
		}
		LOGGER.warn("Could not convert attribute={}, value='{}'", element.getName(), value);
		return value;
	}

	protected Object convertEdmDateTime(final String s, final EdmElement element) throws EdmException
	{
		//"2016-01-21T14:33:17.354-0500"
		if (s.length() == 28 && //
				s.charAt(4) == '-')
		{
			try
			{
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				return sdf.parse(s);
			}
			catch (final ParseException e)
			{
				LOGGER.error("Parsing date attribute={}, value='{}'", element.getName(), s);
				return null;
			}
		}

		//06.12.2016 19:45:17-05:00
		if (s.length() == 25 && //
				s.charAt(2) == '.' && //
				s.charAt(5) == '.')
		{
			try
			{
				final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ssXXX");
				return sdf.parse(s);
			}
			catch (final ParseException e)
			{
				LOGGER.error("Parsing date attribute={}, value='{}'", element.getName(), s);
				return null;
			}
		}

		//03.12.2015 12:00:00
		if (s.length() >= 19 && //
				s.charAt(2) == '.' && //
				s.charAt(5) == '.')
		{
			final int year = Integer.parseInt(s.substring(6, 10));
			final int month = Integer.parseInt(s.substring(3, 5));
			final int dayOfMonth = Integer.parseInt(s.substring(0, 2));
			final int hour = Integer.parseInt(s.substring(11, 13));
			final int minute = Integer.parseInt(s.substring(14, 16));
			final int second = Integer.parseInt(s.substring(17, 19));

			final ZonedDateTime dateTime = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, UTC);
			final long epochMilli = dateTime.toInstant().toEpochMilli();
			final Long from = Long.valueOf(epochMilli);
			LOGGER.debug("Converting attribute={}, value='{}' to {}", element.getName(), s, from);
			return from;
		}
		LOGGER.warn("Could not convert attribute={}, string='{}'", element.getName(), s);
		return s;
	}

	protected Object convertEdmDateTimeOffset(final Object value, final EdmElement element) throws EdmException
	{
		EdmDateTimeOffset.getInstance();
		if (value instanceof Long && //
				Optional.ofNullable(element.getFacets()).map(EdmFacets::getPrecision).filter(i -> i == 0).isPresent())
		{
			// Truncate milliseconds precision.
			final long time = (Long) value;
			return time - (time % 1000);
		}
		return value;
	}

	protected Object convertEdmDecimal(final Object value, final EdmElement element) throws EdmException
	{
		EdmDecimal.getInstance();
		if (value == null || value instanceof Byte || value instanceof Short)
		{
			return value;
		}
		if (value instanceof Integer || value instanceof Long || value instanceof BigInteger)
		{
			return value;
		}

		try
		{
			final BigDecimal bigDecimal = new BigDecimal(value.toString());
			final Integer scale = element.getFacets().getScale();
			if (scale != null && bigDecimal.scale() > scale)
			{
				final BigDecimal bigDecimalScaled = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
				LOGGER.warn("Downscaling attribute={}, value={} to={}.", element.getName(), bigDecimal, bigDecimalScaled);
				return bigDecimalScaled;
			}
			return bigDecimal;
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error("Error parsing attribute '{}' with value='{}'", element.getName(), value, e);
			return Boolean.FALSE.equals(element.getFacets().isNullable()) ? BigDecimal.ZERO : null;
		}
	}

	protected Object convertEdmInt32(final Object value, final EdmElement element) throws EdmException
	{
		EdmInt32.getInstance();
		if (value instanceof Byte || //
				value instanceof Short || //
				value instanceof Integer || //
				value instanceof Long)
		{
			return value;
		}

		if (value instanceof Double)
		{

			final Double valueDouble = (Double) value;
			final double d = valueDouble.doubleValue();
			if (Double.compare(d % 1, 0) == 0)
			{
				return valueDouble.longValue();
			}
			else
			{
				final Long round = Math.round(valueDouble);
				LOGGER.warn("Rounding attribute '{}' from value='{}' to='{}'", element.getName(), value, round);
				return round;
			}
		}

		try
		{
			return Math.round(Double.parseDouble(value.toString()));
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error("Error parsing attribute '{}' with value='{}'", element.getName(), value, e);
			return Integer.valueOf(0);
		}
	}

	protected Object convertEdmString(final Object value, final EdmElement element) throws EdmException
	{
		EdmString.getInstance();
		if (value == null)
		{
			return null;
		}
		final String string = value.toString();
		final Integer maxLength = Optional.ofNullable(element.getFacets()).map(EdmFacets::getMaxLength).orElse(MAX_VALUE);
		if (string.length() > maxLength)
		{
			final String substring = string.substring(0, maxLength);
			LOGGER.warn("Truncating attribute '{}' from {} to {} characters, value='{}' to='{}'.", element.getName(),
					string.length(), maxLength, string, substring);
			return substring;
		}
		return string;
	}

}
