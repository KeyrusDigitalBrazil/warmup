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
package de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.checkout.CheckoutService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;


/**
 *
 */
public class SapOrdermgmtB2BCheckoutFacadeTest
{

	@Mock
	private CheckoutService checkoutService;
	@Mock
	private BackendAvailabilityService backendAvailabilityService;
	@Mock
	private MessageSource messageSource;
	@Mock
	private I18NService i18nService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private ModelService modelService;

	private SapOrdermgmtB2BCheckoutFacade sapOrdermgmtB2BCheckoutFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sapOrdermgmtB2BCheckoutFacade = new SapOrdermgmtB2BCheckoutFacade();
		sapOrdermgmtB2BCheckoutFacade.setCheckoutService(checkoutService);
		sapOrdermgmtB2BCheckoutFacade.setBackendAvailabilityService(backendAvailabilityService);
		sapOrdermgmtB2BCheckoutFacade.setMessageSource(messageSource);
		sapOrdermgmtB2BCheckoutFacade.setI18nService(i18nService);
		sapOrdermgmtB2BCheckoutFacade.setBaseStoreService(baseStoreService);
		sapOrdermgmtB2BCheckoutFacade.setModelService(modelService);

		final ProductImageHelper productImageHelper = Mockito.mock(ProductImageHelper.class);
		sapOrdermgmtB2BCheckoutFacade.setProductImageHelper(productImageHelper);

		final SAPConfigurationModel sapConfigurationModel = new SAPConfigurationModel();
		sapConfigurationModel.setSapordermgmt_enabled(true);

		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setSAPConfiguration(sapConfigurationModel);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStoreModel);

	}


	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#getPaymentTypes()}
	 * .
	 */
	@Test
	public void testGetPaymentTypes()
	{
		final List<B2BPaymentTypeData> paymentTypeList = sapOrdermgmtB2BCheckoutFacade.getPaymentTypes();
		assertEquals(1, paymentTypeList.size());
		assertEquals(CheckoutPaymentType.ACCOUNT.getCode().toLowerCase(), paymentTypeList.get(0).getCode());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#placeOrder()}.
	 *
	 * @throws InvalidCartException
	 */
	@Test
	public void testPlaceOrder() throws InvalidCartException
	{
		sapOrdermgmtB2BCheckoutFacade.placeOrder();
		verify(checkoutService, times(1)).placeOrder();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#placeOrder(de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData)}
	 * .
	 *
	 * @throws InvalidCartException
	 */
	@Test
	public void testPlaceOrderPlaceOrderData() throws InvalidCartException
	{
		sapOrdermgmtB2BCheckoutFacade.placeOrder(null);
		verify(commerceCheckoutService, times(0)).placeOrder(new CommerceCheckoutParameter());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#prepareCartForCheckout()}
	 * .
	 */
	@Test
	public void testPrepareCartForCheckout()
	{
		sapOrdermgmtB2BCheckoutFacade.prepareCartForCheckout();
		verify(checkoutService, times(0)).placeOrder();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#removeDeliveryAddress()}
	 * .
	 */
	@Test
	public void testRemoveDeliveryAddress()
	{
		final boolean isAddressRemoved = sapOrdermgmtB2BCheckoutFacade.removeDeliveryAddress();
		assertEquals(Boolean.FALSE, Boolean.valueOf(isAddressRemoved));
		verify(checkoutService, times(0)).setDeliveryAddress("sapCustomerId");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#removeDeliveryMode()}
	 * .
	 */
	@Test
	public void testRemoveDeliveryMode()
	{
		sapOrdermgmtB2BCheckoutFacade.removeDeliveryMode();
		verify(checkoutService, times(1)).setDeliveryMode("");
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#scheduleOrder(de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData)}
	 * .
	 */
	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testScheduleOrder()
	{
		sapOrdermgmtB2BCheckoutFacade.scheduleOrder(null);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.SapOrdermgmtB2BCheckoutFacade#updateCheckoutCart(de.hybris.platform.commercefacades.order.data.CartData)}
	 * .
	 */
	@Test
	public void testUpdateCheckoutCart()
	{
		sapOrdermgmtB2BCheckoutFacade.updateCheckoutCart(null);
		verify(checkoutService, times(1)).updateCheckoutCart(null);
	}

}
