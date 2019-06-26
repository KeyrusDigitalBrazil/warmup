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
package de.hybris.platform.promotionengineservices.rao.util;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.util.CartRAOBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 *
 */
public class CartRAOBuilderTest
{

	private CartRAOBuilder builder;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		builder = new CartRAOBuilder();

	}

	@Test
	public void testBasicBuilder()
	{
		final CartRAO cart = builder.toCart();
		Assert.assertNotNull(cart.getTotal());
		Assert.assertNotNull(cart.getActions());
		Assert.assertTrue(cart.getActions().isEmpty());
		Assert.assertNotNull(cart.getEntries());
		Assert.assertTrue(cart.getEntries().isEmpty());
	}

}
