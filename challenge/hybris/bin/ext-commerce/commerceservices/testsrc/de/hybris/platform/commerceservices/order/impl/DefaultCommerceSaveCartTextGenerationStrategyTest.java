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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultCommerceSaveCartTextGenerationStrategyTest extends ServicelayerBaseTest
{
	@Resource
	private DefaultCommerceSaveCartTextGenerationStrategy commerceSaveCartTextGenerationStrategy;

	@Resource
	private ModelService modelService;

	private CartModel cartModel;
	private static final String BASE_CART_NAME = "test saved cart";
	private static final String BASE_CART_NAME_COPY = "  test copy saved copy 2 cart  ";
	private static final String EXPECTED_BASE_CART_NAME_COPY = "test copy saved copy 2 cart";
	private static final String COPY_COUNT_REGEX = "(\\s+\\d*)$";
	private final static String SINGLE_WHITE_SPACE = " ";
	private final static String SUFFIX = "copy";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGenerateCloneSaveCartName()
	{
		cartModel = modelService.create(CartModel.class);
		cartModel.setName(BASE_CART_NAME);
		String name = commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
		assertEquals(BASE_CART_NAME + SINGLE_WHITE_SPACE + SUFFIX, name);

		for (int i = 2; i <= 10; i++)
		{
			cartModel.setName(name);
			name = commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
			assertEquals(BASE_CART_NAME + SINGLE_WHITE_SPACE + SUFFIX + SINGLE_WHITE_SPACE + i, name);
		}
	}

	@Test
	public void testGenerateCloneSaveCartNameWithCopySuffix()
	{
		cartModel = modelService.create(CartModel.class);
		cartModel.setName(BASE_CART_NAME_COPY);
		final String name = commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
		assertEquals(EXPECTED_BASE_CART_NAME_COPY + SINGLE_WHITE_SPACE + SUFFIX, name);

		cartModel.setName(name);
		final String nameWithNumIndex = commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel,
				COPY_COUNT_REGEX);
		assertEquals(EXPECTED_BASE_CART_NAME_COPY + SINGLE_WHITE_SPACE + SUFFIX + SINGLE_WHITE_SPACE + 2, nameWithNumIndex);

		final String nameWithSpaceBetweenNumIndex = BASE_CART_NAME_COPY + SINGLE_WHITE_SPACE + SUFFIX + SINGLE_WHITE_SPACE
				+ SINGLE_WHITE_SPACE + SINGLE_WHITE_SPACE + 2;
		cartModel.setName(nameWithSpaceBetweenNumIndex);
		assertEquals(EXPECTED_BASE_CART_NAME_COPY + SINGLE_WHITE_SPACE + SUFFIX + SINGLE_WHITE_SPACE + 3,
				commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX));

	}

	@Test
	public void testGenerateCloneSaveCartNameWithEmptyName()
	{
		cartModel = new CartModel();
		cartModel.setCode("00234411");
		final String name = commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, COPY_COUNT_REGEX);
		assertEquals("00234411", name);
	}

	@Test
	public void testGenerateCloneSaveCartNameWithNullRegex()
	{
		thrown.expect(IllegalArgumentException.class);
		commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(cartModel, null);

	}

	@Test
	public void testGenerateCloneSaveCartNameWithNullCartParam()
	{
		thrown.expect(IllegalArgumentException.class);
		commerceSaveCartTextGenerationStrategy.generateCloneSaveCartName(null, COPY_COUNT_REGEX);
	}
}
