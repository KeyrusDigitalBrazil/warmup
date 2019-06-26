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
package de.hybris.platform.commercefacades.voucher.converters.populator;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.voucher.VoucherService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link OrderAppliedVouchersPopulator}
 */
@UnitTest
public class OrderAppliedVouchersPopulatorTest
{
	private OrderAppliedVouchersPopulator orderAppliedVouchersPopulator;
	@Mock
	private OrderModel orderModel;
	@Mock
	private VoucherService voucherService;

	private Collection<String> appliedVoucherCodes;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		orderAppliedVouchersPopulator = new OrderAppliedVouchersPopulator();
		orderAppliedVouchersPopulator.setVoucherService(voucherService);
		appliedVoucherCodes = new ArrayList(Arrays.asList("SUMMER69", "WINTER16"));
		given(voucherService.getAppliedVoucherCodes(orderModel)).willReturn(appliedVoucherCodes);
	}

	@Test
	public void testPopulate()
	{
		final OrderData orderData = new OrderData();
		orderAppliedVouchersPopulator.populate(orderModel, orderData);

		Assert.assertThat(orderData.getAppliedVouchers(), IsIterableContainingInOrder.contains(appliedVoucherCodes.toArray()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullSource()
	{
		final OrderData orderData = new OrderData();
		orderAppliedVouchersPopulator.populate(null, orderData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateWithNullTarget()
	{
		orderAppliedVouchersPopulator.populate(orderModel, null);
	}

}
