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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.converters.populator.ProductBundlePopulator;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundlefacades.order.converters.comparator.AbstractBundleOrderEntryComparator;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class BundleCartPotentialProductsPopulatorTest
{
	@Mock
	private BundleCommerceCartService bundleCommerceCartService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private ProductBundlePopulator<ProductModel, ProductData> productBundlePopulator;
	@Mock
	private CartService cartService;
	@Mock
	private ConfigurationService configurationService;
	private Collection<AbstractBundleOrderEntryComparator<OrderEntryData>> orderEntryDataComparators;
	@InjectMocks
	private BundleCartPotentialProductsPopulator<CartModel, CartData> populator = new BundleCartPotentialProductsPopulator<>();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		populator.setOrderEntryDataComparators(Collections.singletonList(new AbstractBundleOrderEntryComparator<OrderEntryData>()
		{
			@Override
			protected int doCompare(final OrderEntryData o1, final OrderEntryData o2)
			{
				return 0;
			}

			@Override
			protected boolean comparable(final OrderEntryData o1, final OrderEntryData o2)
			{
				return true;
			}
		}));
		final Configuration cfg = mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(cfg);
		when(cfg.getInt(
				BundleCartPotentialProductsPopulator.POTENTIAL_PRODUCT_COUNT_KEY,
				BundleCartPotentialProductsPopulator.DEFAULT_PRODUCT_COUNT)).thenReturn(Integer.valueOf(2));
	}


	@Test
	public void shouldHandleNullEntityList()
	{
		assertThat(populator.getPotentialProducts(new CartModel(), new CartData()), emptyIterable());
	}

	@Test
	public void shouldSkipNullProducts()
	{
		final CartData cartData = new CartData();
		cartData.setEntries(Collections.singletonList(new OrderEntryData()));
		assertThat(populator.getPotentialProducts(new CartModel(), cartData), emptyIterable());
	}

	@Test
	public void shouldSkipNonBundleProducts()
	{
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setProduct(new ProductData());
		cartData.setEntries(Collections.singletonList(entry));
		assertThat(populator.getPotentialProducts(new CartModel(), cartData), emptyIterable());
	}

	@Test
	public void shouldPopulateBundleProducts()
	{
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData product = new ProductData();
		product.setBundleTemplates(Collections.singletonList(new BundleTemplateData()));
		entry.setProduct(product);
		entry.setEntryNumber(Integer.valueOf(1));
		cartData.setEntries(Collections.singletonList(entry));
		when(cartService.getEntryForNumber(any(), anyInt())).thenReturn(new CartEntryModel());

		assertThat(populator.getPotentialProducts(new CartModel(), cartData), emptyIterable());
		verify(productBundlePopulator).populate(any(), same(product));
	}

	@Test
	public void shouldSkipPromotions()
	{
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData product = new ProductData();
		product.setBundleTemplates(Collections.singletonList(new BundleTemplateData()));
		entry.setProduct(product);
		entry.setEntryNumber(Integer.valueOf(1));
		entry.setBundleNo(ConfigurableBundleServicesConstants.NO_BUNDLE);
		cartData.setEntries(Collections.singletonList(entry));
		when(cartService.getEntryForNumber(any(), anyInt())).thenReturn(new CartEntryModel());

		assertThat(populator.getPotentialProducts(new CartModel(), cartData), emptyIterable());
		verify(productBundlePopulator).populate(any(), same(product));
	}

	@Test
	public void shouldAddPotentialProducts()
	{
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		final ProductData product = new ProductData();
		final BundleTemplateData component = new BundleTemplateData();
		component.setId("test");
		component.setMaxItemsAllowed(5);
		component.setProducts(Collections.singletonList(product));
		product.setBundleTemplates(Collections.singletonList(component));
		entry.setProduct(product);
		entry.setEntryNumber(Integer.valueOf(1));
		entry.setBundleNo(2);
		cartData.setEntries(Collections.singletonList(entry));
		when(cartService.getEntryForNumber(any(), anyInt())).thenReturn(new CartEntryModel());

		final List<OrderEntryData> potentialProducts = populator.getPotentialProducts(new CartModel(), cartData);
		assertThat(potentialProducts, iterableWithSize(1));
		assertEquals(product, potentialProducts.get(0).getProduct());
		assertEquals(component, potentialProducts.get(0).getComponent());
		assertTrue(potentialProducts.get(0).isAddable());
	}
}
