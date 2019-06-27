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
package de.hybris.platform.chineseprofileservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineseprofileservices.strategies.impl.ChineseVerificationCodeGenerationStrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ChineseVerificationCodeGenerationStrategyTest
{

	private ChineseVerificationCodeGenerationStrategy strategy;

	@Before
	public void prepare()
	{
		strategy = new ChineseVerificationCodeGenerationStrategy();
		strategy.setLength(4);
	}

	@Test
	public void test_generate()
	{
		final String code = strategy.generate();
		Assert.assertEquals(4, code.length());
	}
}
