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

package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BOrderApprovalDashboardListPopulatorTest
{
	private static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";

	@Mock
	private OrderModel orderModel;
	@Mock
	private UserModel userModel;
	@Mock
	private CustomerData b2bCustomerData;
	@Mock
	private Converter<UserModel, CustomerData> b2bCustomerConverter;


	private final B2BOrderApprovalDashboardListPopulator b2BOrderApprovalDashboardListPopulator = new B2BOrderApprovalDashboardListPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		b2BOrderApprovalDashboardListPopulator.setB2bCustomerConverter(b2bCustomerConverter);
		when(b2bCustomerConverter.convert(userModel)).thenReturn(b2bCustomerData);
	}

	@Test
	public void testConvert()
	{
		given(orderModel.getPurchaseOrderNumber()).willReturn(PURCHASE_ORDER_NUMBER);
		given(orderModel.getUser()).willReturn(userModel);

		final OrderData orderData = new OrderData();
		b2BOrderApprovalDashboardListPopulator.populate(orderModel, orderData);

		Assert.assertEquals(PURCHASE_ORDER_NUMBER, orderData.getPurchaseOrderNumber());
		verify(b2bCustomerConverter).convert(orderModel.getUser());
		Assert.assertEquals(b2bCustomerData, orderData.getB2bCustomerData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSourceNull()
	{
		b2BOrderApprovalDashboardListPopulator.populate(null, mock(OrderData.class));
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNull()
	{
		b2BOrderApprovalDashboardListPopulator.populate(orderModel, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}
}
