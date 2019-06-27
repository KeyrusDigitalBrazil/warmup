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

import de.hybris.platform.sap.productconfig.facades.IntervalInDomainHelper;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.util.localization.Localization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Default implementation of the {@link IntervalInDomainHelper}.<br>
 */
public class IntervalInDomainHelperImpl implements IntervalInDomainHelper
{
	private static final Pattern INTERVAL_SPLIT_PATTERN = Pattern.compile("([-0-9,.]+)[ ]?-[ ]?([-0-9,.]*)");
	private static final Pattern INFINITY_PATTERN = Pattern.compile("([<>≤≥][=]?[ ]?)([-0-9,.]+)");

	private ValueFormatTranslator valueFormatTranslator;

	@Override
	public String retrieveIntervalMask(final CsticModel cstic)
	{
		final StringBuilder intervalBuffer = new StringBuilder();
		if (cstic.getAssignableValues() != null)
		{
			for (final CsticValueModel valueModel : cstic.getAssignableValues())
			{
				if (valueModel.isDomainValue())
				{
					appendToIntervalMask(intervalBuffer, valueModel);
				}
			}
		}
		return intervalBuffer.toString().trim();

	}

	protected void appendToIntervalMask(final StringBuilder intervalBuffer, final CsticValueModel valueModel)
	{
		if (intervalBuffer.length() > 0)
		{
			intervalBuffer.append(" ; ");
		}
		intervalBuffer.append(formatNumericInterval(valueModel.getName()));
	}


	@Override
	public String formatNumericInterval(final String interval)
	{
		String formattedInterval = interval;
		Matcher match = INTERVAL_SPLIT_PATTERN.matcher(interval.trim());

		if (match.find())
		{
			formattedInterval = getValueFormatTranslator().formatNumeric(match.group(1).trim());
			final String formattedValueMax = getValueFormatTranslator().formatNumeric(match.group(2).trim());
			if (!formattedValueMax.isEmpty())
			{
				formattedInterval = formattedInterval + " - " + formattedValueMax;
			}
		}
		else
		{
			match = INFINITY_PATTERN.matcher(interval);
			if (match.find())
			{
				formattedInterval = match.group(1) + getValueFormatTranslator().formatNumeric(match.group(2).trim());
			}
		}
		return formattedInterval;
	}

	@Override
	public String retrieveErrorMessage(final String value, final String interval)
	{
		return Localization.getLocalizedString("type.ProductConfiguration.IntervalValue.conflict", new Object[]
		{ value, interval });
	}

	protected ValueFormatTranslator getValueFormatTranslator()
	{
		return valueFormatTranslator;
	}

	/**
	 * @param valueFormatTranslator
	 */
	public void setValueFormatTranslator(final ValueFormatTranslator valueFormatTranslator)
	{
		this.valueFormatTranslator = valueFormatTranslator;
	}
}
