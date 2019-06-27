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
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommerceQuoteCartCalculationMethodHookTest
{
	private static final String REGULAR_DISCOUNT_VALUE_CODE = "regularCode";

	@InjectMocks
	public DefaultCommerceQuoteCartCalculationMethodHook defaultCommerceQuoteCartCalculationMethodHook = new DefaultCommerceQuoteCartCalculationMethodHook();

	@Mock
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;
	@Mock
	private CalculationService calculationService;
	@Mock
	private ModelService modelService;

	@Mock
	private CartModel cartModel;

	@Mock
	private List<DiscountValue> quoteDiscounts;

	@Mock
	private List<DiscountValue> regularDiscounts;

	@Before
	public void setup()
	{
		quoteDiscounts = Collections
				.singletonList(DiscountValue.createRelative(CommerceServicesConstants.QUOTE_DISCOUNT_CODE, Double.valueOf(10)));

		regularDiscounts = Collections.singletonList(DiscountValue.createRelative(REGULAR_DISCOUNT_VALUE_CODE, Double.valueOf(10)));

		given(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel)).willReturn(quoteDiscounts);
		given(cartModel.getGlobalDiscountValues()).willReturn(regularDiscounts);
	}

	@Test
	public void shouldAddQuoteDiscounts() throws CalculationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);

		defaultCommerceQuoteCartCalculationMethodHook.afterCalculate(parameter);
		verify(cartModel).setGlobalDiscountValues(ListUtils.union(regularDiscounts, quoteDiscounts));
		verify(calculationService).calculateTotals(cartModel, true);
		verify(modelService).save(cartModel);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddQuoteDiscountsIfParameterIsNull()
	{
		defaultCommerceQuoteCartCalculationMethodHook.afterCalculate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddQuoteDiscountsIfCartModelIsNull()
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(null);
		defaultCommerceQuoteCartCalculationMethodHook.afterCalculate(null);
	}
}
