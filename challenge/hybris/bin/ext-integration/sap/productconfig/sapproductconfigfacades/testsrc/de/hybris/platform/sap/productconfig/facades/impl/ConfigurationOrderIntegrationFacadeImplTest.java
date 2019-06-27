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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationOrderIntegrationFacadeImplTest
{
	private static final String ORDER_CODE = "orderCode";
	private static final int ENTRY_NUMBER = 4;

	private ConfigurationOrderIntegrationFacadeImpl classUnderTest;

	private OrderModel orderModel;
	private ConfigurationOverviewData overviewData;

	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private UserService userService;
	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private ConfigurationAbstractOrderIntegrationHelper configOrderHelper;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationOrderIntegrationFacadeImpl();
		classUnderTest.setCustomerAccountService(customerAccountService);
		classUnderTest.setBaseStoreService(baseStoreService);
		classUnderTest.setUserService(userService);
		classUnderTest.setConfigurationAbstractOrderIntegrationHelper(configOrderHelper);

		orderModel = new OrderModel();
		orderModel.setCode(ORDER_CODE);
		given(Boolean.valueOf(configOrderHelper.isReorderable(orderModel))).willReturn(Boolean.TRUE);


		final BaseStoreModel store = new BaseStoreModel();

		given(baseStoreService.getCurrentBaseStore()).willReturn(store);
		given(customerAccountService.getOrderForCode(ORDER_CODE, store)).willReturn(orderModel);

		overviewData = new ConfigurationOverviewData();
		overviewData.setProductCode(ORDER_CODE);
		given(configOrderHelper.retrieveConfigurationOverviewData(orderModel, ENTRY_NUMBER)).willReturn(overviewData);
	}

	@Test
	public void testFindOrderModel()
	{
		final OrderModel result = classUnderTest.findOrderModel(ORDER_CODE);
		assertNotNull(result);
		assertEquals(orderModel, result);
	}

	@Test
	public void testGetConfiguration()
	{
		final ConfigurationOverviewData result = classUnderTest.getConfiguration(ORDER_CODE, ENTRY_NUMBER);
		assertNotNull(result);
		assertEquals(overviewData, result);
	}

	@Test
	public void testIsReorderable_true()
	{
		assertTrue(classUnderTest.isReorderable(ORDER_CODE));
	}

	@Test
	public void testIsReorderable_false()
	{
		given(Boolean.valueOf(configOrderHelper.isReorderable(orderModel))).willReturn(Boolean.FALSE);
		assertFalse(classUnderTest.isReorderable(ORDER_CODE));
	}

}
