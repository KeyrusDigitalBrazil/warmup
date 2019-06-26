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
package de.hybris.platform.consignmenttrackingservices.interceptor;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConsignmentPrepareInterceptorTest
{

	private ConsignmentPrepareInterceptor interceptor;

	@Mock
	private InterceptorContext ctx;

	private CarrierModel carrier;

	private ConsignmentModel consignment;

	private String carrierCode;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		interceptor = new ConsignmentPrepareInterceptor();
		carrierCode = "TestCarrier";
		carrier = mock(CarrierModel.class);
		consignment = new ConsignmentModel();
	}

	@Test
	public void testOnPrepare() throws InterceptorException
	{
		given(carrier.getCode()).willReturn(carrierCode);
		consignment.setCarrierDetails(carrier);
		interceptor.onPrepare(consignment, ctx);
		Assert.assertTrue(carrier.getCode().equals(consignment.getCarrier()));

		given(carrier.getCode()).willReturn(null);
		consignment.setCarrierDetails(null);
		interceptor.onPrepare(consignment, ctx);
		Assert.assertNull(consignment.getCarrier());
	}
}
