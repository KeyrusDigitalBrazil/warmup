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
package de.hybris.platform.marketplaceservices.interceptor;

import static org.mockito.BDDMockito.given;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.marketplaceservices.strategies.VendorOrderTotalPriceCalculationStrategy;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;


/**
 *
 */
@UnitTest
public class ConsignmentPrepareInterceptorTest
{
	private ConsignmentPrepareInterceptor consignmentPrepareInterceptor;

	@Mock
	private VendorOrderTotalPriceCalculationStrategy vendorOrderTotalPriceCalculationStrategy;

	@Mock
	private InterceptorContext ctx;

	private ConsignmentModel consignment;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		consignmentPrepareInterceptor = new ConsignmentPrepareInterceptor();
		consignment = new ConsignmentModel();
		consignmentPrepareInterceptor.setVendorOrderTotalPriceCalculationStrategy(vendorOrderTotalPriceCalculationStrategy);
	}

	@Test
	public void testOnValidate() throws InterceptorException
	{
		given(vendorOrderTotalPriceCalculationStrategy.calculateTotalPrice(consignment)).willReturn(123.45);

		consignmentPrepareInterceptor.onPrepare(consignment, ctx);

		Assert.assertEquals(123.45, consignment.getTotalPrice(), 0.0001);
	}

}
