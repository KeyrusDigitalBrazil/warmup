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
package de.hybris.platform.assistedservicestorefront.customer360.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicestorefront.customer360.CustomerProfileData;
import de.hybris.platform.assistedservicestorefront.customer360.populators.CustomerProfileDataPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;

@UnitTest
public class CustomerProfileDataPopulatorTest
{
    @InjectMocks
    private CustomerProfileDataPopulator customerProfileDataPopulator;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;
    @Mock
    private CustomerAccountService customerAccountService;

    @Before
    public void setup()
    {
        customerProfileDataPopulator = new CustomerProfileDataPopulator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getModelTest()
    {
        // shipment population
        final String shipPhone = "phone 1";
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final AddressModel defaultShipmentAddress = Mockito.mock(AddressModel.class);
        final AddressData addressData = Mockito.mock(AddressData.class);

        Mockito.when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);
        Mockito.when(defaultShipmentAddress.getPhone1()).thenReturn(shipPhone);
        Mockito.when(customerAccountService.getCreditCardPaymentInfos(customerModel, true)).thenReturn(Collections.emptyList());

        final PaymentInfoModel paymentInfoModel = Mockito.mock(PaymentInfoModel.class);
        final AddressModel defaultBillingAddress = Mockito.mock(AddressModel.class);
        Mockito.when(paymentInfoModel.getBillingAddress()).thenReturn(defaultBillingAddress);
        Mockito.when(defaultBillingAddress.getPhone1()).thenReturn(null);
        Mockito.when(customerModel.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);

        Mockito.when(addressConverter.convert(Mockito.anyObject())).thenReturn(addressData);

        final CustomerProfileData profileData = new CustomerProfileData();
        customerProfileDataPopulator.populate(customerModel, profileData);

        Assert.assertEquals(shipPhone, profileData.getPhone2());
        Assert.assertEquals(addressData, profileData.getDeliveryAddress());
    }
}

