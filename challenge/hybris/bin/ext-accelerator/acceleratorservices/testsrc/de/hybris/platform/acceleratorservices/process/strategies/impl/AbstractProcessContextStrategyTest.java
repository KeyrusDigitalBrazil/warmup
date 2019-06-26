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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Test class for AbstractProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractProcessContextStrategyTest
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
	private BusinessProcessModel businessProcessModel;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private CurrencyModel validCurrencyModel;
	@Mock
	private LanguageModel validLanguageModel;

	@InjectMocks
	private AbstractProcessContextStrategy abstractProcessContextStrategy = new AbstractProcessContextStrategy()
	{
		@Override
		protected CustomerModel getCustomer(BusinessProcessModel businessProcess)
		{
			return customerModel;
		}

		@Override
		public BaseSiteModel getCmsSite(BusinessProcessModel businessProcessModel)
		{
			return cmsSiteModel;
		}
	};

	@Test
	public void testGetContentCatalogVersion()
	{
		given(cmsSiteModel.getContentCatalogs()).willReturn(Collections.singletonList(contentCatalogModel));
		given(contentCatalogModel.getId()).willReturn("test");
		given(catalogVersionService.getSessionCatalogVersionForCatalog("test")).willReturn(catalogVersionModel);

		final CatalogVersionModel result = abstractProcessContextStrategy.getContentCatalogVersion(businessProcessModel);

		Assert.assertEquals(catalogVersionModel, result);
	}

	@Test
	public void testInitializeSessionContextWithCustomerSettings()
	{
		given(customerModel.getSessionCurrency()).willReturn(currencyModel);
		given(customerModel.getSessionLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(languageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));

		abstractProcessContextStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(1)).getAllLanguages();
		verify(commerceCommonI18NService, times(0)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(1)).getAllCurrencies();
		verify(commerceCommonI18NService, times(0)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);
	}

	@Test
	public void testInitializeSessionContextWithSiteDefaultSettings()
	{
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(languageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(currencyModel);
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(languageModel);

		abstractProcessContextStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(0)).getAllLanguages();
		verify(commerceCommonI18NService, times(1)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(0)).getAllCurrencies();
		verify(commerceCommonI18NService, times(1)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);
	}

	@Test
	public void testInitializeSessionContextWithSiteDefaultSettingsAsCustomerSettingsInvalid()
	{
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(validLanguageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(validCurrencyModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(currencyModel);
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(languageModel);
		given(customerModel.getSessionCurrency()).willReturn(currencyModel);
		given(customerModel.getSessionLanguage()).willReturn(languageModel);

		abstractProcessContextStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(1)).getAllLanguages();
		verify(commerceCommonI18NService, times(1)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(1)).getAllCurrencies();
		verify(commerceCommonI18NService, times(1)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);
	}
}
