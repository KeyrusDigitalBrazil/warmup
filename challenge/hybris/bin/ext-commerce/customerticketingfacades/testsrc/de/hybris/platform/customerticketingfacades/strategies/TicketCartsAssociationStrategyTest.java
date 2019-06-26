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
package de.hybris.platform.customerticketingfacades.strategies;

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Test case for TicketCartsAssociationStrategy class.
 */
@UnitTest
public class TicketCartsAssociationStrategyTest
{

	@InjectMocks
	private TicketCartsAssociationStrategy ticketCartAssociationStrategy;

	@Mock
	private Converter<CartModel, TicketAssociatedData> ticketAssociationCoverter;

	/**
	 * Test setup.
	 */
	@Before
	public void setup()
	{
		ticketCartAssociationStrategy = new TicketCartsAssociationStrategy();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Should return an empty map when given user model dose not have any carts.
	 */
	@Test
	public void shouldReturnEmptyMapWhenNoCarts()
	{
		UserModel userModel = Mockito.mock(UserModel.class);
		Mockito.when(userModel.getCarts()).thenReturn(Collections.emptyList());

		Map<String, List<TicketAssociatedData>> result = ticketCartAssociationStrategy.getObjects(userModel);

		Assert.assertEquals(Collections.emptyMap(), result);
	}

	/**
	 * Should return an map which have 2 TicketAssociatedData with Cart key.
	 */
	@Test
	public void shouldReturnCartObjectMap()
	{
		UserModel userModel = Mockito.mock(UserModel.class);
		CartModel cart1 = Mockito.mock(CartModel.class);
		CartModel cart2 = Mockito.mock(CartModel.class);
		TicketAssociatedData data1 = new TicketAssociatedData();
		TicketAssociatedData data2 = new TicketAssociatedData();
		Mockito.when(userModel.getCarts()).thenReturn(Lists.newArrayList(cart1, cart2));
		Mockito.when(ticketAssociationCoverter.convert(cart1)).thenReturn(data1);
		Mockito.when(ticketAssociationCoverter.convert(cart2)).thenReturn(data2);

		Map<String, List<TicketAssociatedData>> result = ticketCartAssociationStrategy.getObjects(userModel);

		Assert.assertEquals(data1, result.get("Cart").get(0));
		Assert.assertEquals(data2, result.get("Cart").get(1));
	}

	/**
	 * Test case for the ticketCartAssociationStrategy conveter getter.
	 */
	@Test
	public void testGetter()
	{
		Assert.assertEquals(ticketAssociationCoverter, ticketCartAssociationStrategy.getTicketAssociationCoverter());
	}


}
