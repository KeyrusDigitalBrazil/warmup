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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



/**
 * Default implementation of the {@link ProductConfigRuleFormatTranslator}.
 */
public class ProductConfigRuleFormatTranslatorImpl implements ProductConfigRuleFormatTranslator
{
	/**
	 * string indicating that no value is assigned to a cstic at all
	 */
	private static final String NO_VALUE = "[NO_VALUE]";

	private static final Logger LOG = Logger.getLogger(ProductConfigRuleFormatTranslatorImpl.class);
	private static final int HIGH_FRACTION_COUNT = 99;

	private static final ThreadLocal<DecimalFormat> serviceFormatCache = new ThreadLocal<DecimalFormat>()
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

	private static final ThreadLocal<DecimalFormat> rulesFormatCache = new ThreadLocal<DecimalFormat>()
	{
		@Override
		protected DecimalFormat initialValue()
		{
			final DecimalFormat numberFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
			numberFormatter.setParseBigDecimal(true);
			numberFormatter.setGroupingUsed(true);
			numberFormatter.setMaximumFractionDigits(HIGH_FRACTION_COUNT);
			return numberFormatter;
		}
	};


	/**
	 * Override this method to use a different decimal format in backoffice UI
	 */
	protected DecimalFormat getRulesFormat()
	{
		return rulesFormatCache.get();
	}

	/**
	 * Override this method if you engine use a different decimal format
	 */
	protected DecimalFormat getServiceFormat()
	{
		return serviceFormatCache.get();
	}


	@Override
	public String formatForRules(final CsticModel cstic, final String value)
	{
		String formattedValue;
		if (null == value || value.isEmpty())
		{
			formattedValue = getNoValueIndicator();
		}
		else if (isNumericValue(cstic))
		{
			final DecimalFormat engineFormat = getServiceFormat();
			final DecimalFormat ruleFormat = getRulesFormat();
			Number number;
			try
			{
				number = engineFormat.parse(value);
				formattedValue = ruleFormat.format(number);
			}
			catch (final ParseException ex)
			{
				LOG.debug("Could not format numeric value from service '" + value
						+ "'; unfomatted string will be used within rule engine.");
				formattedValue = value;
			}
		}
		else
		{
			formattedValue = value;
		}
		return formattedValue;
	}

	@Override
	public String formatForService(final CsticModel cstic, final String value)
	{
		String formattedValue;
		if (isNumericValue(cstic))
		{
			final DecimalFormat engineFormat = getServiceFormat();
			final DecimalFormat ruleFormat = getRulesFormat();
			Number number;
			try
			{
				number = ruleFormat.parse(value);
				formattedValue = engineFormat.format(number);
			}
			catch (final ParseException ex)
			{
				LOG.debug("Could not format numeric value from rule engine '" + value
						+ "'; unfomatted string will be used within service.");
				formattedValue = value;
			}
		}
		else
		{
			formattedValue = value;
		}
		return formattedValue;
	}

	@Override
	public boolean canBeFormattedForService(final CsticModel cstic, final String value)
	{
		boolean formatable = true;
		if (!StringUtils.isEmpty(value) && isNumericValue(cstic))
		{
			try
			{
				final DecimalFormat ruleFormat = getRulesFormat();
				ruleFormat.parse(value);
			}
			catch (final ParseException ex)
			{
				formatable = false;
			}

		}
		return formatable;
	}



	protected boolean isNumericValue(final CsticModel cstic)
	{
		return !cstic.isConstrained()
				&& (CsticModel.TYPE_INTEGER == cstic.getValueType() || CsticModel.TYPE_FLOAT == cstic.getValueType());
	}

	@Override
	public String getNoValueIndicator()
	{
		return NO_VALUE;
	}
}
