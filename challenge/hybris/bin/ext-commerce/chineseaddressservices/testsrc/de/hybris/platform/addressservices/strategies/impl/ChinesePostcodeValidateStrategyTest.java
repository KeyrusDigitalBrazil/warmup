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

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ChinesePostcodeValidateStrategyTest
{

	private ChinesePostcodeValidateStrategy postcodeValidateStrategy;

	private String regex;

	@Before
	public void prepare()
	{
		postcodeValidateStrategy = new ChinesePostcodeValidateStrategy();
		
	}

	@Test
	public void test_validate()
	{
		regex = "\\d{6}";
		postcodeValidateStrategy.setRegex(regex);

		String postcode = "610041";
		boolean result = postcodeValidateStrategy.validate(postcode);
		Assert.assertTrue(result);

		postcode = "6100411";
		result = postcodeValidateStrategy.validate(postcode);
		Assert.assertFalse(result);

		postcode = "a61004";
		result = postcodeValidateStrategy.validate(postcode);
		Assert.assertFalse(result);

		regex = "";
		postcodeValidateStrategy.setRegex(regex);
		postcode = "a61004";
		result = postcodeValidateStrategy.validate(postcode);
		Assert.assertTrue(result);
	}
}
