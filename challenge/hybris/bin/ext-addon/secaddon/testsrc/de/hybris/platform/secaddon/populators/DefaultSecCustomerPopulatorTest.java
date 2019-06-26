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
package de.hybris.platform.secaddon.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

@UnitTest
public class DefaultSecCustomerPopulatorTest
{
    private static final String CUSTOMER_ID = "customerId";
    DefaultSecCustomerPopulator secCustomerPopulator = new DefaultSecCustomerPopulator();

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPopulateCustomerId()
    {

        //given
        CustomerModel customerModel = buildCustomer();
        CustomerData customerData = new CustomerData();

        // when
        secCustomerPopulator.populate(customerModel, customerData);

        //then
        assertEquals(CUSTOMER_ID, customerData.getCustomerId());

    }

    private CustomerModel buildCustomer()
    {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setCustomerID(CUSTOMER_ID);
        return customerModel;
    }
}
