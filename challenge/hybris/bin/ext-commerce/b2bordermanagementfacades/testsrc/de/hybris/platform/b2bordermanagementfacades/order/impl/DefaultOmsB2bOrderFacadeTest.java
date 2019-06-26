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
package de.hybris.platform.b2bordermanagementfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordermanagementfacades.BaseOrdermanagementFacadeTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOmsB2bOrderFacadeTest extends BaseOrdermanagementFacadeTest
{
	@InjectMocks
	private DefaultOmsB2bOrderFacade omsB2bOrderFacade;
	@Mock
	private OrderEntryModel orderEntry;
	@Mock
	private B2BCostCenterModel b2bCostCenterModel;
	@Mock
	private B2BCommerceCostCenterService b2bCommerceCostCenterService;
	@Mock
	private B2BCustomerModel b2bCustomerModel;

	private static final String COST_CENTER_CODE = "COST_CENTER_1";

	@Before
	public void setup()
	{
		omsB2bOrderFacade.setImpersonationService(impersonationService);
		omsB2bOrderFacade.setOrderService(orderService);
		omsB2bOrderFacade.setBaseSiteService(baseSiteService);
		omsB2bOrderFacade.setBaseStoreService(baseStoreService);

		omsB2bOrderFacade.setCustomerReverseConverter(customerReverseConverter);
		omsB2bOrderFacade.setOrderConverter(orderConverter);
		omsB2bOrderFacade.setOrderRequestReverseConverter(orderRequestReverseConverter);
		//B2B specific
		omsB2bOrderFacade.setB2bOrderRequestReverseConverter(orderRequestReverseConverter);
		when(orderRequestData.getCostCenterCode()).thenReturn(COST_CENTER_CODE);
		when(b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(b2bCostCenterModel);
		when(userService.getUserForUID(USER_UID)).thenReturn(b2bCustomerModel);

	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullOrderRequestData()
	{
		omsB2bOrderFacade.submitOrder(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullStoreUid()
	{
		prepareOrderRequestData();
		when(orderRequestData.getStoreUid()).thenReturn(null);
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullSiteUid()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getSiteUid()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullDeliveryModeCode()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryModeCode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullLanguageIsoCode()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getLanguageIsocode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullCurrencyIsoCode()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getCurrencyIsocode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureIsNotCalculated()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.isCalculated()).thenReturn(false);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullExternalOrderCode()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getExternalOrderCode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullUser()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getUser()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullUserUid()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getUser().getUid()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullAddressData()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullAddressTown()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress().getTown()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullAddressCountry()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress().getCountry()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullAddressPostalCode()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress().getPostalCode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullAddressLine()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress().getLine1()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureEmptyCompanyNameOrName()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getDeliveryAddress().getCompanyName()).thenReturn(null);
		when(orderRequestData.getDeliveryAddress().getFirstName()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void submitOrder_FailureNullPaymentTransactions_NullCostCenter()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getCostCenterCode()).thenReturn(null);
		when(orderRequestData.getPaymentTransactions()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitOrder_Success_PaymentTransactionsNull_WithCostCenter()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getPaymentTransactions()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitOrder_SuccessNullPaymentTransactionsPaymentInfo_withCostCenter()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getPaymentTransactions().get(0).getPaymentInfo()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void submitOrder_FailureNullPaymentTransactionsEntries()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getPaymentTransactions().get(0).getEntries()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void submitOrder_FailureNullOrderEntryData()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getEntries()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullOrderEntryDataEntryNumber()
	{
		prepareOrderRequestData();
		//When
		when(orderEntryRequestData.getEntryNumber()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullOrderEntryDataUnitCode()
	{
		prepareOrderRequestData();
		//When
		when(orderEntryRequestData.getUnitCode()).thenReturn(null);
		//then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}


	@Test(expected = IllegalArgumentException.class)
	public void submitOrder_FailureNullOrderEntryDataProductCode()
	{
		prepareOrderRequestData();
		//When
		when(orderEntryRequestData.getProductCode()).thenReturn(null);
		//Then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void submitOrder_FailureWrongCustomerData()
	{
		prepareOrderRequestData();
		when(userService.getUserForUID(USER_UID)).thenReturn(customerModel);
		//Given
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitOrder_Success()
	{
		prepareOrderRequestData();
		//Then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitB2cOrder_Success()
	{
		prepareOrderRequestData();
		//When
		when(orderRequestData.getCostCenterCode()).thenReturn(null);
		when(customerData.getFirstName()).thenReturn(FIRST_NAME);
		when(customerData.getLastName()).thenReturn(LAST_NAME);
		when(userService.getUserForUID(USER_UID)).thenReturn(userModel);
		//Then
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitB2cOrder_Success_B2bCustomerAndCostCenterNull()
	{
		prepareOrderRequestData();
		when(orderRequestData.getCostCenterCode()).thenReturn(null);
		//When
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void submitB2cOrder_Fail_B2bCustomerAndInvalidCostCenter_NotSaved()
	{
		prepareOrderRequestData();
		when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntry));
		when(b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(null);
		try
		{
			omsB2bOrderFacade.submitOrder(orderRequestData);
		}
		catch (final UnknownIdentifierException e)
		{
			verify(modelService, times(0)).save(orderModel);
		}
	}

	@Test(expected = UnknownIdentifierException.class)
	public void submitB2cOrder_Fail_B2bCustomerAndInvalidCostCenter()
	{
		prepareOrderRequestData();
		when(orderModel.getEntries()).thenReturn(Arrays.asList(orderEntry));
		when(b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(null);
		omsB2bOrderFacade.submitOrder(orderRequestData);
	}

	@Test
	public void getCostCenterById_valid_success()
	{
		when(b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(b2bCostCenterModel);
		omsB2bOrderFacade.getCostCenterById(COST_CENTER_CODE);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCostCenterById_invalid_failure()
	{
		when(b2bCommerceCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).thenReturn(null);
		omsB2bOrderFacade.getCostCenterById(COST_CENTER_CODE);
	}
}
