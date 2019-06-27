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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link DefaultCommerceCheckoutService}. Contains tests moved from
 * {@link DefaultBundleCommerceCheckoutServiceTest} which rely on using
 * {@link BundleCommerceCartService}.
 */
@UnitTest
public class DefaultBundleCommerceCheckoutServiceCSTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CommerceCheckoutService defaultCommerceCheckoutService;
	private CartModel masterCart;

	@Mock
	private BundleCommerceCartService bundleCommerceCartService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		final CalculationService calculationService = mock(CalculationService.class);
		masterCart = mock(CartModel.class);
		final ExternalTaxesService externalTaxesService = mock(ExternalTaxesService.class);

		defaultCommerceCheckoutService = new DefaultCommerceCheckoutService();
		final CommercePlaceOrderStrategy defaultCommercePlaceOrderStrategy = new DefaultCommercePlaceOrderStrategy();

		((DefaultCommercePlaceOrderStrategy) defaultCommercePlaceOrderStrategy).setCalculationService(calculationService);
		((DefaultCommercePlaceOrderStrategy) defaultCommercePlaceOrderStrategy).setExternalTaxesService(externalTaxesService);

		((DefaultCommerceCheckoutService) defaultCommerceCheckoutService)
				.setCommercePlaceOrderStrategy(defaultCommercePlaceOrderStrategy);
	}

	@Test
	public void testPlaceOrderWhenMasterCartIsCalculatedIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(null);
		given(bundleCommerceCartService.getFirstInvalidComponentInCart(masterCart)).willReturn(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setCart(masterCart);

		defaultCommerceCheckoutService.placeOrder(parameter);
	}

	@Test
	public void testPlaceOrderWhenCustomerIsNull() throws InvalidCartException
	{
		given(masterCart.getCalculated()).willReturn(Boolean.TRUE);
		given(bundleCommerceCartService.getFirstInvalidComponentInCart(masterCart)).willReturn(null);

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Customer model cannot be null");

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setCart(masterCart);

		defaultCommerceCheckoutService.placeOrder(parameter);
	}

}