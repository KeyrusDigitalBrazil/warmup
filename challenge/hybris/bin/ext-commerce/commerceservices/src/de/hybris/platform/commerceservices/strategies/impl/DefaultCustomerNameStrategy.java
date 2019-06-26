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
package de.hybris.platform.commerceservices.strategies.impl;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;

import org.apache.commons.lang.StringUtils;


public class DefaultCustomerNameStrategy implements CustomerNameStrategy
{
	private static final String SEPARATOR_SPACE = " ";

	@Override
	public String[] splitName(final String name)
	{
		final String trimmedName = StringUtils.trimToNull(name);
		return new String[] { StringUtils.substringBeforeLast(trimmedName, SEPARATOR_SPACE),
				StringUtils.substringAfterLast(trimmedName, SEPARATOR_SPACE) };
	}

	@Override
	public String getName(final String firstName, final String lastName)
	{
		final String result = StringUtils.trimToEmpty(firstName) + SEPARATOR_SPACE + StringUtils.trimToEmpty(lastName);
		return StringUtils.trimToNull(result);
	}
}
