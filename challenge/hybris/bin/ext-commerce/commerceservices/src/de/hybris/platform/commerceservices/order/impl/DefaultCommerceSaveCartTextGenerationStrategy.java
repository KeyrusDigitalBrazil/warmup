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
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.commerceservices.order.CommerceSaveCartTextGenerationStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.util.localization.Localization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of the {@link CommerceSaveCartTextGenerationStrategy}. It generates only simple texts for
 * saved cart's name and description and should be replaced by a more complex strategy in case more sophisticated texts
 * are needed.
 */
public class DefaultCommerceSaveCartTextGenerationStrategy implements CommerceSaveCartTextGenerationStrategy
{
	private final static String singleWhiteSpaceSeparator = " ";
	private final static String splitSuffixForClone = "\\s+";

	@Override
	public String generateSaveCartName(final CartModel cartToBeSaved)
	{
		return cartToBeSaved.getCode();
	}

	@Override
	public String generateSaveCartDescription(final CartModel cartToBeSaved)
	{
		return "-";
	}

	@Override
	public String generateCloneSaveCartName(final CartModel savedCartToBeCloned, final String copyCountRegex)
	{
		validateParameterNotNull(savedCartToBeCloned,"saved cart parameter cannot be null");
		validateParameterNotNull(copyCountRegex,"regex parameter cannot be null");
		final String baseCartName = StringUtils.trim(savedCartToBeCloned.getName());

		if(StringUtils.isNotBlank(baseCartName))
		{
			final StringBuilder nameBuilder = new StringBuilder();
			final String cloneSuffix = Localization.getLocalizedString("commerceservices.cart.copy");

			if (StringUtils.endsWithIgnoreCase(baseCartName, cloneSuffix))
			{
				appendSaveCartNameToStringBuilder(nameBuilder, new String[]
						{ baseCartName, singleWhiteSpaceSeparator, String.valueOf(2) });
			}
			else
			{
				final Pattern copyNumberPrefixedPattern = Pattern.compile(cloneSuffix + copyCountRegex);
				final Matcher copySuffixWithNumber = copyNumberPrefixedPattern.matcher(baseCartName);

				if (copySuffixWithNumber.find())
				{
					final String matchedSuffix = copySuffixWithNumber.group();
					final String prefixCartName = StringUtils.removeEndIgnoreCase(baseCartName,matchedSuffix);
					final String[] suffixArray = matchedSuffix.split(splitSuffixForClone);
					int copyCount = Integer.parseInt(suffixArray[1]);
					appendSaveCartNameToStringBuilder(nameBuilder, new String[]
							{ prefixCartName.trim(),singleWhiteSpaceSeparator,cloneSuffix,singleWhiteSpaceSeparator, String.valueOf(++copyCount) });
				}
				else
				{
					appendSaveCartNameToStringBuilder(nameBuilder, new String[]
							{ baseCartName, singleWhiteSpaceSeparator, cloneSuffix });
				}
			}
			return nameBuilder.toString();
		}

		return generateSaveCartName(savedCartToBeCloned);
	}

	protected StringBuilder appendSaveCartNameToStringBuilder(final StringBuilder nameBuilder, final String... args)
	{
		for (final String stringToAppend : args)
		{
			nameBuilder.append(stringToAppend);
		}
		return nameBuilder;
	}

}
