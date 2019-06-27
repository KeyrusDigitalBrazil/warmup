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

package de.hybris.platform.personalizationaddon.data.impl;

import org.junit.Assert;
import org.junit.Test;


public class Base64ValueCoderTest
{

	Base64ValueCoder base64ViewValueCoder = new Base64ValueCoder();

	@Test
	public void test()
	{
		//given
		final String input = "LOCATION PL SL GliwiceąŌ∑ę®†ī¨^Ļ„‚ĺ…łŻ∆ķ©ń∂śąążźć√ļńĶ≤≥ļńĶ";

		//when
		final String encode = base64ViewValueCoder.encode(input);

		//then
		Assert.assertNotEquals(input, encode);

		//when
		final String decode = base64ViewValueCoder.decode(encode);

		//then
		Assert.assertEquals(input, decode);
	}
}
