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


import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCommerceAddToCartStrategyTest
{
	@InjectMocks
	private final DefaultCommerceAddToCartStrategy addToCartStrategy = new DefaultCommerceAddToCartStrategy();
	@Mock
	private EntryMergeStrategy entryMergeStrategy;
	@Mock
	private ProductService productService;
	@Mock
	private CartService cartService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CommerceStockService commerceStockService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCartCalculationStrategy calculationStrategy;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSkipMergeIfThereIsNoEntry() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final CommerceCartModification modification = new CommerceCartModification();

		addToCartStrategy.mergeEntry(modification, parameter);

		verify(entryMergeStrategy, never()).getEntryToMerge(any(), any());
	}

	@Test
	public void shouldSkipMergeIfQuantityIsZero() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final CommerceCartModification modification = new CommerceCartModification();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setQuantity(Long.valueOf(0L));

		addToCartStrategy.mergeEntry(modification, parameter);

		verify(entryMergeStrategy, never()).getEntryToMerge(any(), any());
	}

	@Test
	public void shouldSkipMergeIfQuantityIsNull() throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		final CommerceCartModification modification = new CommerceCartModification();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setQuantity(null);

		addToCartStrategy.mergeEntry(modification, parameter);

		verify(entryMergeStrategy, never()).getEntryToMerge(any(), any());
	}

	@Test
	public void shouldFailIfRequestedMergeTargetDoesNotExist() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1);
		final ProductModel product = new ProductModel();
		product.setUnit(new UnitModel());
		parameter.setProduct(product);
		parameter.setQuantity(1);
		when(entryMergeStrategy.getEntryToMerge(any(), any())).thenReturn(null);
		final CartEntryModel entry = new CartEntryModel();
		entry.setOrder(cart);
		when(cartService.addNewEntry(any(), any(), any(Long.class).longValue(), any(), any(Integer.class).intValue(),
				any(Boolean.class).booleanValue())).thenReturn(entry);
		thrown.expect(CommerceCartModificationException.class);
		thrown.expectMessage("The new entry can not be merged into the entry #1");

		addToCartStrategy.addToCart(parameter);
	}

	@Test
	public void shouldNotMergeIsNewEntryIsForced() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setQuantity(2);
		final ProductModel product = new ProductModel();
		product.setUnit(new UnitModel());
		parameter.setProduct(product);
		parameter.setCreateNewEntry(true);
		final CartEntryModel newEntry = new CartEntryModel();
		newEntry.setQuantity(Long.valueOf(1L));
		newEntry.setEntryNumber(Integer.valueOf(1));
		when(cartService.addNewEntry(any(), any(), any(Long.class).longValue(), any(), any(Integer.class).intValue(),
				any(Boolean.class).booleanValue())).thenReturn(newEntry);

		addToCartStrategy.addToCart(parameter);

		verify(entryMergeStrategy, never()).getEntryToMerge(any(), any());
	}

	@Test
	public void shouldMergeEntries() throws CommerceCartModificationException
	{
		final CartModel cart = new CartModel();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setQuantity(2);
		final ProductModel product = new ProductModel();
		product.setUnit(new UnitModel());
		parameter.setProduct(product);
		final AbstractOrderEntryModel existingEntry = new AbstractOrderEntryModel();
		existingEntry.setQuantity(Long.valueOf(1L));
		existingEntry.setEntryNumber(Integer.valueOf(1));
		when(entryMergeStrategy.getEntryToMerge(any(), any())).thenReturn(existingEntry);
		final CartEntryModel newEntry = new CartEntryModel();
		newEntry.setOrder(cart);
		newEntry.setQuantity(Long.valueOf(parameter.getQuantity()));
		newEntry.setEntryNumber(Integer.valueOf(2));
		when(cartService.addNewEntry(any(), any(), any(Long.class).longValue(), any(), any(Integer.class).intValue(),
				any(Boolean.class).booleanValue())).thenReturn(newEntry);
		final ArgumentCaptor<Map> updateQuantityCaptor = ArgumentCaptor.forClass(Map.class);

		final CommerceCartModification modification = addToCartStrategy.addToCart(parameter);

		verify(cartService).updateQuantities(eq(cart), updateQuantityCaptor.capture());
		assertEquals(existingEntry, modification.getEntry());
		assertEquals(Long.valueOf(0L), updateQuantityCaptor.getValue().get(newEntry.getEntryNumber()));
		assertEquals(Long.valueOf(3L), updateQuantityCaptor.getValue().get(existingEntry.getEntryNumber()));
	}

	@Test
	public void batchAddingShouldAlsoMerge() throws CommerceCartMergingException
	{
		final CartModel cart = new CartModel();
		final ProductModel product = new ProductModel();
		product.setUnit(new UnitModel());
		product.setMaxOrderQuantity(Integer.valueOf(100));
		final CommerceCartParameter parameter1 = new CommerceCartParameter();
		parameter1.setCart(cart);
		parameter1.setQuantity(1);
		parameter1.setProduct(product);
		final CommerceCartParameter parameter2 = new CommerceCartParameter();
		parameter2.setCart(cart);
		parameter2.setQuantity(2);
		parameter2.setProduct(product);
		final CartEntryModel entry1 = new CartEntryModel();
		entry1.setOrder(cart);
		entry1.setProduct(product);
		entry1.setEntryNumber(Integer.valueOf(1));
		entry1.setQuantity(Long.valueOf(1L));
		final CartEntryModel entry2 = new CartEntryModel();
		entry2.setOrder(cart);
		entry2.setProduct(product);
		entry2.setEntryNumber(Integer.valueOf(2));
		entry2.setQuantity(Long.valueOf(2L));
		when(entryMergeStrategy.getEntryToMerge(any(), eq(entry1))).thenReturn(entry2);
		when(cartService.addNewEntry(any(), any(), eq(1L), any(), any(Integer.class).intValue(), any(Boolean.class).booleanValue()))
				.thenReturn(entry1);
		when(cartService.addNewEntry(any(), any(), eq(2L), any(), any(Integer.class).intValue(), any(Boolean.class).booleanValue()))
				.thenReturn(entry2);
		final ArgumentCaptor<Map> updateQuantityCaptor = ArgumentCaptor.forClass(Map.class);

		final List<CommerceCartModification> result = addToCartStrategy.addToCart(Arrays.asList(parameter1, parameter2));

		assertThat(result, iterableWithSize(2));
		assertEquals(entry2, result.get(0).getEntry());
		assertEquals(entry2, result.get(1).getEntry());
		verify(cartService).updateQuantities(eq(cart), updateQuantityCaptor.capture());
		assertEquals(Long.valueOf(0L), updateQuantityCaptor.getValue().get(entry1.getEntryNumber()));
		assertEquals(Long.valueOf(3L), updateQuantityCaptor.getValue().get(entry2.getEntryNumber()));
	}

}
