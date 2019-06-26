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
package de.hybris.platform.assistedservicefacades.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer.converters.populator.CustomersListCustomerPopulator;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
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

import java.util.ArrayList;
import java.util.List;

@UnitTest
public class CustomersListCustomerPopulatorTest
{
    @InjectMocks
    private CustomersListCustomerPopulator populator;

    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private Converter<AddressModel, AddressData> addressConverter;
    @Mock
    private Converter<MediaModel, ImageData> imageConverter;
    @Mock
    private AssistedServiceService assistedServiceService;

    @Before
    public void setup()
    {
        populator = new CustomersListCustomerPopulator();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotPopulateEmptyCart()
    {
        final AddressModel addressModel = Mockito.mock(AddressModel.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final CustomerData data = new CustomerData();
        final String cartId = "cardId";

        Mockito.when(cartModel.getCode()).thenReturn(cartId);
        Mockito.when(customerAccountService.getDefaultAddress(customerModel)).thenReturn(addressModel);
        Mockito.when(assistedServiceService.getLatestModifiedCart(customerModel)).thenReturn(cartModel);

        populator.populate(customerModel, data);

        Assert.assertEquals(null, data.getLatestCartId());
    }

    @Test
    public void shouldNotPopulateNonEmptyCart()
    {
        final AddressModel addressModel = Mockito.mock(AddressModel.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final AbstractOrderEntryModel entry = Mockito.mock(AbstractOrderEntryModel.class);
        final List<AbstractOrderEntryModel> list = new ArrayList<AbstractOrderEntryModel>();
        list.add(entry);
        final CustomerData data = new CustomerData();
        final String cartId = "cardId";

        Mockito.when(customerAccountService.getDefaultAddress(customerModel)).thenReturn(addressModel);
        Mockito.when(assistedServiceService.getLatestModifiedCart(customerModel)).thenReturn(cartModel);
        Mockito.when(cartModel.getEntries()).thenReturn(list);
        Mockito.when(cartModel.getCode()).thenReturn(cartId);

        populator.populate(customerModel, data);

        Assert.assertEquals(data.getLatestCartId(), cartId);
    }
}
