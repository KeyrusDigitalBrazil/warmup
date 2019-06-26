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
package de.hybris.platform.ruleengineservices.calculation.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.Order;
import de.hybris.platform.ruleengineservices.calculation.NumberedLineItem;
import de.hybris.platform.ruleengineservices.rao.AbstractOrderRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;


@UnitTest
public class DefaultRuleEngineCalculationServiceFindMethodsTest
{
	@InjectMocks
	private DefaultRuleEngineCalculationService service;

	@Before
	public void setUp()
	{
		service = new DefaultRuleEngineCalculationService();
		initMocks(this);
	}

	@Test
	public void testFindOrderEntryRAOValidationOfNullOrder() throws Exception
	{
		try
		{
			service.findOrderEntryRAO(null, null);
			fail("IllegalArgumentException expected.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("order must not be null"));
		}
	}

	@Test
	public void testFindOrderEntryRAOValidationOfNullLineItem() throws Exception
	{
		try
		{
			service.findOrderEntryRAO(mock(AbstractOrderRAO.class), null);
			fail("IllegalArgumentException expected.");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("lineItem must not be null"));
		}
	}

	@Test
	public void testFindOrderEntryRAOWithNullOrderEntries() throws Exception
	{
		final AbstractOrderRAO orderRAO = mock(AbstractOrderRAO.class);
		final NumberedLineItem numberedLineItem = mock(NumberedLineItem.class);

		//explicitly return null:
		when(orderRAO.getEntries()).thenReturn(null);

		assertThat(service.findOrderEntryRAO(orderRAO, numberedLineItem), is(nullValue()));
	}

	@Test
	public void testFindOrderEntryRAOWithOrderEntryWithoutEntryNumber() throws Exception
	{
		final AbstractOrderRAO orderRAO = mock(AbstractOrderRAO.class);
		final NumberedLineItem numberedLineItem = mock(NumberedLineItem.class);

		final OrderEntryRAO orderEntryRAO = mock(OrderEntryRAO.class);
		//explicitly return null:
		when(orderEntryRAO.getEntryNumber()).thenReturn(null);
		final Set<OrderEntryRAO> item = Collections.singleton(orderEntryRAO);
		when(orderRAO.getEntries()).thenReturn(item);

		assertThat(service.findOrderEntryRAO(orderRAO, numberedLineItem), is(nullValue()));
	}

	@Test
	public void testFindOrderEntryRAOWithOrderEntryWithoutMatchingEntryNumber() throws Exception
	{
		final Integer entryNumber1 = Integer.valueOf(234);
		final Integer entryNumber2 = Integer.valueOf(123);

		final AbstractOrderRAO orderRAO = mock(AbstractOrderRAO.class);
		final NumberedLineItem numberedLineItem = mock(NumberedLineItem.class);
		when(numberedLineItem.getEntryNumber()).thenReturn(entryNumber1);
		final OrderEntryRAO orderEntryRAO = mock(OrderEntryRAO.class);
		//explicitly return null:
		when(orderEntryRAO.getEntryNumber()).thenReturn(entryNumber2);
		final Set<OrderEntryRAO> item = Collections.singleton(orderEntryRAO);
		when(orderRAO.getEntries()).thenReturn(item);

		assertThat(service.findOrderEntryRAO(orderRAO, numberedLineItem), is(nullValue()));
	}

	@Test
	public void testFindOrderEntryRAOWithOrderEntryWithAMatchingEntryNumber() throws Exception
	{
		final Integer entryNumber1 = Integer.valueOf(234);
		final Integer entryNumber2 = entryNumber1;

		final AbstractOrderRAO orderRAO = mock(AbstractOrderRAO.class);
		final NumberedLineItem numberedLineItem = mock(NumberedLineItem.class);
		when(numberedLineItem.getEntryNumber()).thenReturn(entryNumber1);
		final OrderEntryRAO orderEntryRAO = mock(OrderEntryRAO.class);
		//explicitly return null:
		when(orderEntryRAO.getEntryNumber()).thenReturn(entryNumber2);
		final Set<OrderEntryRAO> item = Collections.singleton(orderEntryRAO);
		when(orderRAO.getEntries()).thenReturn(item);

		assertThat(service.findOrderEntryRAO(orderRAO, numberedLineItem), is(orderEntryRAO));
	}

	@Test
	public void testFindLineItemValidation()
	{
		try
		{
			service.findLineItem(null, null);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("cart must not be null"));
		}
		try
		{
			service.findLineItem(mock(Order.class), null);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("entry rao must not be null"));
		}
		try
		{
			final OrderEntryRAO entryRao = new OrderEntryRAO();
			entryRao.setEntryNumber(null);
			service.findLineItem(mock(Order.class), entryRao);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("entry rao must have an entry number!"));
		}
	}

	@Test
	public void testFindLineItemVerificationNoneFound()
	{
		final Order mock = mock(Order.class);
		final OrderEntryRAO entryRao = new OrderEntryRAO();
		entryRao.setEntryNumber(Integer.valueOf(123));
		try
		{
			service.findLineItem(mock, entryRao);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("can't find corresponding LineItem for the given orderEntryRao:" + entryRao));
		}
	}

	@Test
	public void testFindLineItemVerificationNoneOfCorrectTypeFound()
	{
		final Order cart = mock(Order.class);
		final OrderEntryRAO entryRao = new OrderEntryRAO();
		entryRao.setEntryNumber(Integer.valueOf(123));
		final LineItem incorrectTypeLineItem = mock(LineItem.class);
		final List<LineItem> incorrectTypeLineItems = Collections.singletonList(incorrectTypeLineItem);
		when(cart.getLineItems()).thenReturn(incorrectTypeLineItems);

		try
		{
			service.findLineItem(cart, entryRao);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("can't find corresponding LineItem for the given orderEntryRao:" + entryRao));
		}
	}

	@Test
	public void testFindLineItemVerificationCorrectTypeNoMatchFound()
	{
		final Order cart = mock(Order.class);
		final OrderEntryRAO entryRao = new OrderEntryRAO();
		entryRao.setEntryNumber(Integer.valueOf(123));
		final NumberedLineItem incorrectTypeLineItem = mock(NumberedLineItem.class);
		//different entry number
		when(incorrectTypeLineItem.getEntryNumber()).thenReturn(Integer.valueOf(456));
		final List<LineItem> incorrectTypeLineItems = Collections.singletonList(incorrectTypeLineItem);
		when(cart.getLineItems()).thenReturn(incorrectTypeLineItems);

		try
		{
			service.findLineItem(cart, entryRao);
			fail("expected IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("can't find corresponding LineItem for the given orderEntryRao:" + entryRao));
		}
	}

	@Test
	public void testFindLineItemVerificationCorrectTypeOneMatchFound()
	{
		final Order cart = mock(Order.class);
		final OrderEntryRAO entryRao = new OrderEntryRAO();
		final Integer entryNumber = Integer.valueOf(123);
		entryRao.setEntryNumber(entryNumber);
		final NumberedLineItem numberedLineItem = mock(NumberedLineItem.class);
		//different entry number
		when(numberedLineItem.getEntryNumber()).thenReturn(entryNumber);
		final List<LineItem> incorrectTypeLineItems = Collections.singletonList(numberedLineItem);
		when(cart.getLineItems()).thenReturn(incorrectTypeLineItems);

		final NumberedLineItem findLineItem = service.findLineItem(cart, entryRao);
		assertThat(findLineItem, is(numberedLineItem));
	}

}
