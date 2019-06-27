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
package de.hybris.platform.assistedserviceservices.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@UnitTest
public class CommerceCustomerSupportServiceTest
{
    @InjectMocks
    private DefaultCommerceCustomerSupportService service;

    @Mock
    private AssistedServiceService assistedServiceService;

    @Before
    public void setup()
    {
        service = new DefaultCommerceCustomerSupportService();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnAgentAndCustomerForCurrentSession()
    {
        final AssistedServiceSession serviceSessionMock = Mockito.mock(AssistedServiceSession.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final EmployeeModel employeeModel = Mockito.mock(EmployeeModel.class);

        Mockito.when(serviceSessionMock.getAgent()).thenReturn(employeeModel);
        Mockito.when(serviceSessionMock.getEmulatedCustomer()).thenReturn(customerModel);
        Mockito.when(assistedServiceService.getAsmSession()).thenReturn(serviceSessionMock);

        Assert.assertEquals(customerModel, service.getEmulatedCustomer());
        Assert.assertEquals(employeeModel, service.getAgent());
    }
}
