/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.product.ProductService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class BundleCartPotentialProductDisableRulePopulatorTest
{
	@Mock
	private ProductService productService;
	@Mock
	private BundleCommerceCartService bundleCommerceCartService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@InjectMocks
	private BundleCartPotentialProductDisableRulePopulator<CartModel, CartData> populator
			= new BundleCartPotentialProductDisableRulePopulator<>();

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCheckThatDataIsNotNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter cartData");

		populator.populate(new CartModel(), null);
	}

	@Test
	public void shouldSkipEmptyData()
	{
		populator.populate(null, new CartData());

		verify(productService, never()).getProductForCode(any());
	}

	@Test
	public void shouldSkipBundleEntries()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(1);
		entry.setAddable(true);
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		verify(productService, never()).getProductForCode(any());
	}

	@Test
	public void shouldSkipEmptyEntries()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		entry.setProduct(null);
		entry.setAddable(true);
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		verify(productService, never()).getProductForCode(any());
	}

	@Test
	public void shouldSkipDisableProducts()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		final ProductData product = new ProductData();
		product.setDisabled(true);
		entry.setAddable(true);
		entry.setProduct(product);
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		verify(productService, never()).getProductForCode(any());
	}

	@Test
	public void shouldSkipEntriesWithoutComponent()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		final ProductData product = new ProductData();
		product.setDisabled(false);
		entry.setProduct(product);
		entry.setAddable(true);
		entry.setComponent(null);
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		verify(productService, never()).getProductForCode(any());
	}

	@Test
	public void shouldSkipEnabledProducts()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		final ProductData product = new ProductData();
		product.setDisabled(false);
		entry.setProduct(product);
		entry.setAddable(true);
		entry.setComponent(new BundleTemplateData());
		when(bundleCommerceCartService.checkAndGetReasonForDisabledProductInComponent(any(), any(), any(), anyInt(), anyBoolean()))
				.thenReturn(null);
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		assertFalse(product.isDisabled());
	}

	@Test
	public void shouldIgnoreNotAddable()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		final ProductData product = new ProductData();
		product.setDisabled(false);
		entry.setProduct(product);
		entry.setAddable(false);
		entry.setComponent(new BundleTemplateData());
		when(bundleCommerceCartService.checkAndGetReasonForDisabledProductInComponent(any(), any(), any(), anyInt(), anyBoolean()))
				.thenReturn("");
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		assertFalse(product.isDisabled());
	}

	@Test
	public void shouldDisableProducts()
	{
		final CartData cart = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		final ProductData product = new ProductData();
		product.setDisabled(false);
		entry.setProduct(product);
		entry.setAddable(true);
		entry.setComponent(new BundleTemplateData());
		when(bundleCommerceCartService.checkAndGetReasonForDisabledProductInComponent(any(), any(), any(), anyInt(), anyBoolean()))
				.thenReturn("");
		cart.setEntries(Collections.singletonList(entry));

		populator.populate(null, cart);

		assertTrue(product.isDisabled());
		assertFalse(entry.isAddable());
		assertFalse(entry.isRemoveable());
	}
}
