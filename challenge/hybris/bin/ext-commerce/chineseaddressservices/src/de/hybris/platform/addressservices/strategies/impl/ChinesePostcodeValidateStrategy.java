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
package de.hybris.platform.addressservices.strategies.impl;

import de.hybris.platform.addressservices.strategies.PostcodeValidateStrategy;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * A implementation for validating Chinese post code
 */
public class ChinesePostcodeValidateStrategy implements PostcodeValidateStrategy
{

	private String regex;

	@Override
	public boolean validate(final String postcode)
	{

		if (StringUtils.isNotBlank(regex))
		{
			return Pattern.compile(regex).matcher(postcode).matches();
		}

		return true;
	}

	protected String getRegex()
	{
		return regex;
	}

	@Required
	public void setRegex(final String regex)
	{
		this.regex = regex;
	}
}
