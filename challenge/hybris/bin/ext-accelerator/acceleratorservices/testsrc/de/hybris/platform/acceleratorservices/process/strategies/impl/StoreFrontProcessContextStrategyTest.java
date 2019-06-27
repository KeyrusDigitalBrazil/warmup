/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Test class for StoreFrontProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StoreFrontProcessContextStrategyTest
{
	@Mock
	private StoreFrontCustomerProcessModel storeFrontProcessModel;

	@Mock
	private BusinessProcessModel businessProcessModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private CurrencyModel customerCurrency;

	@Mock
	private CurrencyModel defaultCustomerCurrency;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;

	@InjectMocks
	private StoreFrontProcessContextStrategy strategy = new StoreFrontProcessContextStrategy();

	@Before
	public void setUp() throws Exception
	{
		given(storeFrontProcessModel.getSite()).willReturn(baseSiteModel);
		given(storeFrontProcessModel.getCustomer()).willReturn(customerModel);
	}

	@Test
	public void testGetCmsSiteFromStoreFrontProcess() throws Exception
	{
		final BaseSiteModel resultSite = strategy.getCmsSite(storeFrontProcessModel);

		assertSame(baseSiteModel, resultSite);
	}

	@Test
	public void testGetCmsSiteFromNonStoreFrontProcess() throws Exception
	{
		final BaseSiteModel resultSite = strategy.getCmsSite(businessProcessModel);

		assertNull(resultSite);
	}

	@Test
	public void testSetCurrencyFromCustomer() throws Exception
	{
		given(customerModel.getSessionCurrency()).willReturn(customerCurrency);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Arrays.asList(customerCurrency));

		strategy.setCurrency(storeFrontProcessModel);

		verify(commonI18NService).setCurrentCurrency(customerCurrency);
	}

	@Test
	public void testSetCurrencyFromDefaultCurrency() throws Exception
	{
		given(customerModel.getSessionCurrency()).willReturn(null);
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(defaultCustomerCurrency);

		strategy.setCurrency(storeFrontProcessModel);

		verify(commonI18NService).setCurrentCurrency(defaultCustomerCurrency);
	}
}
