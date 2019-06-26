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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultProductResolutionStrategyTest
{


	private static final String PRODUCT_CODE = "123";
	private static final String PRODUCT_NAME = "The Product";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@InjectMocks
	private DefaultProductResolutionStrategy strategy;

	@Mock
	private PromotionResultModel promotionResult;

	@Mock
	private RuleParameterData data;

	@Mock
	private ProductService productService;

	@Mock
	private ProductModel product;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNullDataValue()
	{
		// given
		BDDMockito.given(data.getValue()).willReturn(null);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		// then
		Assert.assertNull(result);
	}

	@Test
	public void testWrongDataValueType()
	{
		// given
		BDDMockito.given(data.getValue()).willReturn(Integer.valueOf(2));

		//expect
		expectedException.expect(ClassCastException.class);

		//when
		strategy.getValue(data, promotionResult, Locale.US);

	}

	@Test
	public void testProductResolution()
	{
		//given
		BDDMockito.given(data.getValue()).willReturn(PRODUCT_CODE);
		BDDMockito.given(product.getName()).willReturn(PRODUCT_NAME);
		BDDMockito.given(productService.getProductForCode(Mockito.anyString())).willReturn(product);

		//when
		final String result = strategy.getValue(data, promotionResult, Locale.US);

		//then
		Assert.assertEquals(PRODUCT_NAME, result);

	}
}
