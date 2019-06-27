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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CommerceCartParameterBasicPopulatorTest
{
	@Mock
	private ProductService productService;
	@Mock
	private CartService cartService;
	@Mock
	private PointOfServiceService pointOfServiceService;

	@InjectMocks
	private CommerceCartParameterBasicPopulator populator;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAssignCurrentCart()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();
		when(cartService.getSessionCart()).thenReturn(mock(CartModel.class));

		populator.populate(source, target);

		assertNotNull(target.getCart());
	}

	@Test
	public void shouldEnableHooks()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();

		populator.populate(source, target);

		assertTrue(target.isEnableHooks());
	}

	@Test
	public void shouldEnableEntryMerge()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();

		populator.populate(source, target);

		assertFalse(target.isCreateNewEntry());
	}

	@Test
	public void shouldPopulateQuantity()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();
		source.setQuantity(1L);

		populator.populate(source, target);

		assertEquals(source.getQuantity(), target.getQuantity());
	}

	@Test
	public void shouldPopulateEntryGroupNumber()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();
		source.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(2))));

		populator.populate(source, target);

		assertEquals(source.getEntryGroupNumbers(), target.getEntryGroupNumbers());
	}

	@Test
	public void shouldPopulateStore()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();
		source.setStoreId("testStore");
		when(pointOfServiceService.getPointOfServiceForName("testStore")).thenReturn(mock(PointOfServiceModel.class));

		populator.populate(source, target);

		assertNotNull(target.getPointOfService());
	}

	@Test
	public void shouldPopulateProduct()
	{
		final AddToCartParams source = new AddToCartParams();
		final CommerceCartParameter target = new CommerceCartParameter();
		source.setProductCode("testProduct");
		final ProductModel product = mock(ProductModel.class);
		when(productService.getProductForCode("testProduct")).thenReturn(product);
		when(product.getUnit()).thenReturn(mock(UnitModel.class));

		populator.populate(source, target);

		assertTrue(product == target.getProduct());
		assertTrue(product.getUnit() == target.getUnit());
	}

}
