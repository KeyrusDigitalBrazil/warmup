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
package com.sap.hybris.saprevenuecloudproduct.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.saprevenuecloudproduct.dao.SapRevenueCloudProductDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;


/**
 * JUnit test suite for {@link DefaultSapRevenueCloudProductServiceTest}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapRevenueCloudProductServiceTest
{

	SubscriptionPricePlanModel pricePlan;

	@Mock
	private SapRevenueCloudProductDao sapRevenueCloudProductDao;


	@InjectMocks
	private DefaultSapRevenueCloudProductService defaultSapRevenueCloudProductService;

	@Before
	public void setUp() throws Exception
	{
		pricePlan = new SubscriptionPricePlanModel();
	}

	@Test
	public void checkIfPricePlanIsReturnedForValidPricePlanAndCatalogVersion()
	{
		when(sapRevenueCloudProductDao.getSubscriptionPricePlanForId(any(String.class), any(CatalogVersionModel.class)))
				.thenReturn(Optional.of(pricePlan));
		final SubscriptionPricePlanModel pricePlanRes = defaultSapRevenueCloudProductService
				.getSubscriptionPricePlanForId("dummyPricePlan", new CatalogVersionModel());
		assertNotNull(pricePlanRes);
	}


}