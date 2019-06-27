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
package de.hybris.platform.subscriptionservices.order.hook;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SubscriptionUpdateCartEntryHookTest
{
	@InjectMocks
	private SubscriptionUpdateCartEntryHook hook;

	@Mock
	private SubscriptionProductService subscriptionProductService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCartCalculationStrategy calculationStrategy;
	@Mock
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;

	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAcceptMasterCartsOnly()
	{
		final CartModel parentCart = new CartModel();
		final CartModel childCart = new CartModel();
		childCart.setParent(parentCart);
		childCart.setCode("CHILD");
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(childCart);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Provided cart 'CHILD' is not a master cart");

		hook.beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldFailIfCartIsEmpty()
	{
		final CartModel cart = new CartModel();
		cart.setCode("TEST");
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1L);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Cart TEST has no entries");

		hook.beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldFailIfEntryNotFound()
	{
		final CartModel cart = new CartModel();
		cart.setCode("TEST");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(2));
		cart.setEntries(Collections.singletonList(entry));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1L);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Entry #1 was not found in cart TEST");

		hook.beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldWorkForMasterCartsOnly()
	{
		final CartModel parentCart = new CartModel();
		final CartModel childCart = new CartModel();
		childCart.setParent(parentCart);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("is not a master cart");
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(childCart);

		hook.afterUpdateCartEntry(parameter, new CommerceCartModification());
	}

	@Test
	public void shouldSkipPlainProducts()
	{
		final CartModel cart = new CartModel();
		cart.setCode("TEST");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(1));
		cart.setEntries(Collections.singletonList(entry));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1L);
		parameter.setQuantity(0L);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.FALSE);
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(entry);

		hook.afterUpdateCartEntry(parameter, modification);

		verify(modelService, never()).remove(any());
	}

	@Test
	public void shouldRejectNegativeQuantity()
	{
		final CartModel cart = new CartModel();
		cart.setCode("TEST");
		final ProductModel product = new ProductModel();
		product.setCode("PRODUCT");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(1));
		entry.setProduct(product);
		cart.setEntries(Collections.singletonList(entry));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1L);
		parameter.setQuantity(-1L);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Subscription product 'PRODUCT' must have a new quantity of 0 or 1, quantity given: -1");

		hook.beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldRejectQuantityGreaterThatOne()
	{
		final CartModel cart = new CartModel();
		cart.setCode("TEST");
		final ProductModel product = new ProductModel();
		product.setCode("PRODUCT");
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(1));
		entry.setProduct(product);
		cart.setEntries(Collections.singletonList(entry));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1L);
		parameter.setQuantity(2L);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Subscription product 'PRODUCT' must have a new quantity of 0 or 1, quantity given: 2");

		hook.beforeUpdateCartEntry(parameter);
	}

	@Test
	public void shouldRemoveEmptyChildCarts()
	{
		final CartModel parentCart = new CartModel();
		parentCart.setCode("MASTER");
		final AbstractOrderEntryModel parentEntry = new AbstractOrderEntryModel();
		parentEntry.setEntryNumber(Integer.valueOf(1));
		parentCart.setEntries(Collections.singletonList(parentEntry));
		parentEntry.setOrder(parentCart);
		final CartModel childCart1 = new CartModel();
		childCart1.setCode("CHILD1");
		childCart1.setEntries(Collections.emptyList());
		childCart1.setParent(parentCart);
		final CartModel childCart2 = new CartModel();
		childCart2.setCode("CHILD2");
		childCart2.setEntries(Collections.singletonList(parentEntry));
		childCart2.setParent(parentCart);
		parentCart.setChildren(Arrays.asList(childCart1, childCart2));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(parentCart);
		parameter.setEntryNumber(1L);
		parameter.setQuantity(0L);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(parentEntry);

		hook.afterUpdateCartEntry(parameter, modification);

		verify(modelService).remove(childCart1);
		verify(modelService, never()).remove(childCart2);
	}

	@Test
	public void shouldUpdateQuantityOfChildEntries()
	{
		final CartModel parentCart = new CartModel();
		parentCart.setCode("MASTER");
		final AbstractOrderEntryModel parentEntry = new AbstractOrderEntryModel();
		parentEntry.setEntryNumber(Integer.valueOf(1));
		parentCart.setEntries(Collections.singletonList(parentEntry));
		parentEntry.setOrder(parentCart);
		final AbstractOrderEntryModel childEntry = new AbstractOrderEntryModel();
		parentEntry.setChildEntries(Collections.singletonList(childEntry));
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(parentEntry);
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(parentCart);
		parameter.setEntryNumber(1L);
		parameter.setQuantity(1L);
		when(Boolean.valueOf(subscriptionProductService.isSubscription(any()))).thenReturn(Boolean.TRUE);

		hook.afterUpdateCartEntry(parameter, modification);

		assertEquals(1, childEntry.getQuantity().intValue());
	}


}
