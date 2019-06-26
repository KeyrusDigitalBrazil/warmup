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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Test class for AbstractOrderProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractOrderProcessContextStrategyTest
{
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private SessionService sessionService;
	@Mock
	private OrderModel orderModel;
	@Mock
	private CMSSiteModel cmsSiteModel;
	@Mock
	private ContentCatalogModel contentCatalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private OrderProcessModel orderProcessModel;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private LanguageModel customerLanguage;
	@Mock
	private LanguageModel orderLanguage;
	@Mock
	private LanguageModel defaultLanguage;
	@Mock
	private CurrencyModel validCurrencyModel = mock(CurrencyModel.class);
	@Mock
	private LanguageModel validLanguageModel = mock(LanguageModel.class);

	@InjectMocks
	private AbstractOrderProcessContextStrategy contextStrategy = new AbstractOrderProcessContextStrategy()
	{
		@Override
		protected Optional<OrderModel> getOrderModel(BusinessProcessModel businessProcessModel)
		{
			return Optional.of(orderModel);
		}
	};

	@Before
	public void setUp() throws Exception
	{
		given(orderModel.getSite()).willReturn(cmsSiteModel);
		given(orderModel.getUser()).willReturn(customerModel);
	}

	@Test
	public void shouldGetCMSSiteFromOrder()
	{
		Assert.assertEquals(cmsSiteModel, contextStrategy.getCmsSite(orderProcessModel));
	}

	@Test
	public void shouldSetLanguageFromOrderWhenPresent() throws Exception
	{
		given(orderModel.getLanguage()).willReturn(orderLanguage);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singletonList(orderLanguage));

		contextStrategy.setLanguage(orderProcessModel);

		verify(commonI18NService).setCurrentLanguage(orderLanguage);
	}

	@Test
	public void shouldSetLanguageFromCustomerWhenLanguageNotInOrder() throws Exception
	{
		given(customerModel.getSessionLanguage()).willReturn(customerLanguage);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singletonList(customerLanguage));

		contextStrategy.setLanguage(orderProcessModel);

		verify(commonI18NService).setCurrentLanguage(customerLanguage);
	}

	@Test
	public void shouldSetDefaultCommerceLanguageWhenNonePresent() throws Exception
	{
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(defaultLanguage);

		contextStrategy.setLanguage(orderProcessModel);

		verify(commonI18NService).setCurrentLanguage(defaultLanguage);
	}
}
