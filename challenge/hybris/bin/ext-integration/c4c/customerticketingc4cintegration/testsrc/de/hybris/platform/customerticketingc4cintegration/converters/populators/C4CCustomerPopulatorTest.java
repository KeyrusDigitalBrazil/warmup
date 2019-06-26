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
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test case for C4CCustomerPopulator.
 */
@UnitTest
public class C4CCustomerPopulatorTest
{
	private C4CCustomerPopulator populator;

	/**
	 * Should populate customer id correctly.
	 */
	@Test
	public void shouldPopulateCustomerId()
	{
		final String customerId = "customer-id";

		CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		Mockito.when(customerModel.getCustomerID()).thenReturn(customerId);
		populator = new C4CCustomerPopulator();

		CustomerData customerData = new CustomerData();
		populator.populate(customerModel, customerData);

		Assert.assertEquals(customerId, customerData.getCustomerId());
	}
}
