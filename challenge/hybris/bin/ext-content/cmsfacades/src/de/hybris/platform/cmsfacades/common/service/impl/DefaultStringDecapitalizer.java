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
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Default implementation of {@link StringDecapitalizer}.
 */
public class DefaultStringDecapitalizer implements StringDecapitalizer
{
	@Override
	public Optional<String> decapitalize(final Class<?> theClass)
	{
		if (theClass == null)
		{
			return Optional.empty();
		}
		return Optional.of(decapitalizeString(theClass.getSimpleName()));
	}

	/**
	 * Convert class names to XML names based on the rules defined in the JAXB specification to convert the first
	 * letter(s) to lower case.
	 *
	 * @param className
	 *           - the class name to be decapitalized
	 * @return the transformed class name
	 */
	protected String decapitalizeString(final String className)
	{
		final char[] chars = className.toCharArray();

		final Pattern pattern = Pattern.compile("^([A-Z]+)");
		final Matcher matcher = pattern.matcher(className);
		if (matcher.find())
		{
			final String upperCaseNameStart = matcher.group(0);
			int length = upperCaseNameStart.length();

			/**
			 * Don't include the last letter in the nameStart group to be converted to lower case if the character
			 * following the last letter is another letter. For example CMSParagraph, the nameStart group is CMSP, and only
			 * CMS is converted. The converted value is cmsParagraph.
			 * <p>
			 * If the character following the last letter is a number or special character, the whole nameStart group is to
			 * be converted. For example CMS407Paragraph, the nameStartGroup is CMS, and the entire group is converted. The
			 * converted value is cms407Paragraph.
			 */
			if (length < chars.length && Character.isLetter(chars[length]))
			{
				length = length - 1;
			}

			length = length > 0 ? length - 1 : length; //needed so that we can use <= in the loop below
			for (int i = 0; i <= length; i++)
			{
				chars[i] = Character.toLowerCase(chars[i]);
			}
		}
		return new String(chars);
	}

}
