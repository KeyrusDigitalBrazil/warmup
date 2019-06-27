/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementfacades;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.impl.DefaultOrderService;
import de.hybris.platform.ordermanagementfacades.order.data.OrderEntryRequestData;
import de.hybris.platform.ordermanagementfacades.order.data.OrderRequestData;
import de.hybris.platform.ordermanagementfacades.order.impl.DefaultOmsOrderFacade;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionData;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.impl.DefaultBaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BaseOrdermanagementFacadeTest
{
	@InjectMocks
	private DefaultOmsOrderFacade baseFacade;
	@Mock
	protected DefaultBaseSiteService baseSiteService;
	@Mock
	protected DefaultBaseStoreService baseStoreService;
	@Mock
	protected ImpersonationService impersonationService;
	@Mock
	protected DefaultOrderService orderService;
	@Mock
	protected EnumerationService enumerationService;
	@Mock
	protected OrderModel orderModel;
	@Mock
	protected OrderModel orderModel2;
	@Mock
	protected Converter<OrderModel, OrderData> orderConverter;
	@Mock
	protected ModelService modelService;
	@Mock
	protected UserService userService;
	@Mock
	protected Converter<CustomerData, CustomerModel> customerReverseConverter;
	@Mock
	protected Converter<OrderRequestData, OrderModel> orderRequestReverseConverter;
	@Mock
	protected OrderProcessModel orderProcessModel;
	@Mock
	protected CustomerModel customerModel;
	@Mock
	protected BaseStoreModel baseStoreModel;
	@Mock
	protected BaseSiteModel baseSiteModel;
	@Mock
	protected UserModel userModel;
	@Mock
	protected OrderRequestData orderRequestData;
	@Mock
	protected CustomerData customerData;
	@Mock
	protected AddressData addressData;
	@Mock
	protected CountryData countryData;
	@Mock
	protected PaymentTransactionData paymentTransactionData;
	@Mock
	protected CCPaymentInfoData ccPaymentInfoData;
	@Mock
	protected PaymentTransactionEntryData paymentTransactionEntryData;
	@Mock
	protected OrderEntryRequestData orderEntryRequestData;
	@Mock
	protected BusinessProcessService businessProcessService;
	@Mock
	protected GenericDao<OrderModel> orderGenericDao;

	protected static final String ORDER_PROCESS = "order-process";
	protected static final String ORDER_ID = "ORDER_1";
	protected static final String ORDER_ID_2 = "ORDER_2";
	protected static final String SITE_UID = "SITE_1";
	protected static final String STORE_UID = "STORE_1";
	protected static final String USER_UID = "USER_1";
	protected static final String FIRST_NAME = "FIRSTNAME";
	protected static final String LAST_NAME = "LASTNAME";
	protected static final String COMPANY_NAME = "COMPANY_1";
	protected static final String VERSION_ID = "0";

	@Before
	public void setupBase()
	{
		when(modelService.create(CustomerModel.class)).thenReturn(customerModel);
		when(orderRequestReverseConverter.convert(orderRequestData)).thenReturn(orderModel);
		when(orderProcessModel.getCode()).thenReturn(ORDER_PROCESS);
		when(baseSiteService.getBaseSiteForUID(SITE_UID)).thenReturn(baseSiteModel);
		when(baseStoreService.getBaseStoreForUid(STORE_UID)).thenReturn(baseStoreModel);
		when(baseSiteModel.getStores()).thenReturn(Arrays.asList(baseStoreModel));
		when(orderGenericDao.find(anyMap())).thenReturn(Arrays.asList(orderModel));
		when(orderModel.getCode()).thenReturn(ORDER_ID);
		when(orderModel2.getCode()).thenReturn(ORDER_ID_2);
		baseFacade.setOrderGenericDao(orderGenericDao);
	}

	@Test(expected = ModelNotFoundException.class)
	public void getOrderModelForCode_onlySnapshots_failure()
	{
		when(orderModel.getVersionID()).thenReturn(VERSION_ID);
		when(orderModel2.getVersionID()).thenReturn(VERSION_ID);
		when(orderGenericDao.find(anyMap())).thenReturn(Arrays.asList(orderModel, orderModel2));
		baseFacade.getOrderModelForCode(ORDER_ID);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void getOrderModelForCode_multipleResults_failure()
	{
		when(orderModel.getVersionID()).thenReturn(null);
		when(orderModel2.getVersionID()).thenReturn(null);
		when(orderGenericDao.find(anyMap())).thenReturn(Arrays.asList(orderModel, orderModel2));
		baseFacade.getOrderModelForCode(ORDER_ID);
	}

	@Test
	public void getOrderModelForCode_oneSnapshot_success()
	{
		when(orderModel.getVersionID()).thenReturn(VERSION_ID);
		when(orderModel2.getVersionID()).thenReturn(null);
		when(orderGenericDao.find(anyMap())).thenReturn(Arrays.asList(orderModel, orderModel2));
		baseFacade.getOrderModelForCode(ORDER_ID);
		assertEquals(ORDER_ID_2, baseFacade.getOrderModelForCode(ORDER_ID).getCode());
	}

	protected void prepareOrderRequestData()
	{
		doNothing().when(modelService).save(any());

		// Order
		when(orderModel.getStatus()).thenReturn(OrderStatus.WAIT_FRAUD_MANUAL_CHECK);
		when(orderModel.getOrderProcess()).thenReturn(Arrays.asList(orderProcessModel));
		when(orderModel.getCode()).thenReturn(ORDER_ID);

		when(orderRequestData.getStoreUid()).thenReturn(STORE_UID);
		when(orderRequestData.getSiteUid()).thenReturn(SITE_UID);
		when(orderRequestData.getDeliveryModeCode()).thenReturn("");
		when(orderRequestData.getLanguageIsocode()).thenReturn("");
		when(orderRequestData.getCurrencyIsocode()).thenReturn("");
		when(orderRequestData.isCalculated()).thenReturn(true);
		when(orderRequestData.getExternalOrderCode()).thenReturn(ORDER_ID);


		// Customer Data
		when(customerData.getFirstName()).thenReturn(FIRST_NAME);
		when(customerData.getLastName()).thenReturn(LAST_NAME);
		when(customerData.getUid()).thenReturn(USER_UID);
		when(orderRequestData.getUser()).thenReturn(customerData);

		// Addresses
		when(addressData.getTown()).thenReturn("");
		when(addressData.getCountry()).thenReturn(countryData);
		when(addressData.getPostalCode()).thenReturn("");
		when(addressData.getLine1()).thenReturn("");
		when(addressData.getFirstName()).thenReturn(FIRST_NAME);
		when(addressData.getLastName()).thenReturn(LAST_NAME);
		when(addressData.getCompanyName()).thenReturn(COMPANY_NAME);
		when(orderRequestData.getDeliveryAddress()).thenReturn(addressData);

		// Transactions
		when(paymentTransactionData.getPaymentInfo()).thenReturn(ccPaymentInfoData);
		when(paymentTransactionData.getEntries()).thenReturn(Arrays.asList(paymentTransactionEntryData));
		when(orderRequestData.getPaymentTransactions()).thenReturn(Arrays.asList(paymentTransactionData));

		// Entry
		when(orderEntryRequestData.getEntryNumber()).thenReturn(0);
		when(orderEntryRequestData.getUnitCode()).thenReturn("");
		when(orderEntryRequestData.getProductCode()).thenReturn("");
		when(orderRequestData.getEntries()).thenReturn(Arrays.asList(orderEntryRequestData));

		//Base Stores and Sites
		when(baseStoreModel.getSubmitOrderProcessCode()).thenReturn(ORDER_PROCESS);
		when(orderModel.getStore()).thenReturn(baseStoreModel);

		// User Existing
		when(userService.isUserExisting(USER_UID)).thenReturn(true);
	}
}
