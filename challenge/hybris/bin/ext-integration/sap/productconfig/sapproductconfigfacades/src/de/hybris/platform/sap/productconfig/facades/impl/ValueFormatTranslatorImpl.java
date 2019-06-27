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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link ValueFormatTranslator}.
 */
public class ValueFormatTranslatorImpl implements ValueFormatTranslator
{

	private static final Logger LOG = Logger.getLogger(ValueFormatTranslatorImpl.class);
	private static final int HIGH_FRACTION_COUNT = 99;
	private I18NService i18NService;

	private static final ThreadLocal<Map<Locale, DecimalFormat>> decimalFormatCache = new ThreadLocal()
	{
		@Override
		protected Map<Locale, DecimalFormat> initialValue()
		{
			return new HashMap<>();
		}
	};

	private static final ThreadLocal<DecimalFormat> serviceFormatCache = new ThreadLocal()
	{
		@Override
		protected DecimalFormat initialValue()
		{
			final DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
			numberFormatter.setParseBigDecimal(true);
			numberFormatter.setGroupingUsed(false);
			numberFormatter.setMaximumFractionDigits(HIGH_FRACTION_COUNT);
			numberFormatter.setMinimumFractionDigits(1);
			return numberFormatter;

		}
	};

	@Override
	public String parse(final UiType uiType, final String value)
	{
		final String parsedValue;

		if (UiType.NUMERIC == uiType)
		{
			parsedValue = parseNumeric(value);
		}
		else
		{
			parsedValue = value;
		}
		if (LOG.isDebugEnabled())
		{
			final String msg = String.format("Formatted value [INPUT_VALUE='%s'; PARSED_VALUE='%s'; UI_TYPE='%s']", value,
					parsedValue, uiType);
			LOG.debug(msg);
		}

		return parsedValue;
	}

	@Override
	public String parseNumeric(final String value)
	{
		if (value == null || value.isEmpty())
		{
			return "";
		}

		final String parsedValue;
		final Locale locale = getLocale();
		final BigDecimal number;
		try
		{
			DecimalFormat numberFormatter = createFormatterForUI(locale);
			number = (BigDecimal) numberFormatter.parse(value);

			numberFormatter = createFormatterForService();
			parsedValue = numberFormatter.format(number);
		}
		catch (final ParseException e)
		{
			LOG.debug("Could not parse numeric value '" + value + "'");
			return "";
		}
		return parsedValue;
	}

	@Override
	public String formatNumeric(final String value)
	{
		if (null == value)
		{
			return "";
		}

		final String formattedValue;
		final Locale locale = getLocale();
		final BigDecimal number;
		try
		{
			DecimalFormat numberFormatter = createFormatterForService();
			number = (BigDecimal) numberFormatter.parse(value);

			numberFormatter = createFormatterForUI(locale);
			formattedValue = numberFormatter.format(number);
		}
		catch (final ParseException e)
		{
			LOG.debug("Could not format numeric value '" + value + "'");
			return value;
		}
		return formattedValue;
	}

	@Override
	public String formatDate(final Date date)
	{
		if (date == null)
		{
			return "";
		}

		return SimpleDateFormat.getDateInstance(DateFormat.SHORT, getLocale()).format(date);
	}

	// have a guard for the tests
	protected Locale getLocale()
	{
		final Locale locale;
		final I18NService i18nService = getI18NService();
		if (i18nService == null)
		{
			locale = Locale.ENGLISH;
		}
		else
		{
			locale = i18nService.getCurrentLocale();
		}
		return locale;
	}

	protected DecimalFormat createFormatterForUI(final Locale locale)
	{

		final Map<Locale, DecimalFormat> formatCache = decimalFormatCache.get();
		DecimalFormat numberFormatter = formatCache.get(locale);
		if (numberFormatter == null)
		{
			numberFormatter = (DecimalFormat) NumberFormat.getInstance(locale);
			numberFormatter.setGroupingUsed(true);
			numberFormatter.setParseBigDecimal(true);
			numberFormatter.setMaximumFractionDigits(HIGH_FRACTION_COUNT);
			formatCache.put(locale, numberFormatter);
		}
		return numberFormatter;
	}

	protected DecimalFormat createFormatterForService()
	{
		return serviceFormatCache.get();
	}

	@Override
	public String format(final CsticModel cstic, final String value)
	{
		final String formattedValue;
		if (isNumericCsticType(cstic))
		{
			formattedValue = formatNumeric(value);
		}
		else
		{
			formattedValue = value;
		}

		if (LOG.isDebugEnabled())
		{
			final String msg = String.format("Formatted value [INPUT_VALUE='%s'; FORMATTED_VALUE='%s'; CSTIC_VALUE_TYPE='%d']",
					value, formattedValue, Integer.valueOf(cstic.getValueType()));
			LOG.debug(msg);
		}
		return formattedValue;
	}

	@Override
	public boolean isNumericCsticType(final CsticModel model)
	{
		return CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType();
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

}
