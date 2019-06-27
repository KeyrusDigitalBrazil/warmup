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

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * Test class for QuoteProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuoteProcessContextStrategyTest
{
	@Mock
	private QuoteModel quoteModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private CurrencyModel quoteCurrency;

	@Mock
	private CurrencyModel defaultCurrency;

	@Mock
	private CurrencyModel userCurrency;

	@Mock
	private QuoteProcessModel quoteProcessModel;

	@Mock
	private LanguageModel customerLanguageModel;

	@Mock
	private LanguageModel defaultLanguage;

	@Mock
	private QuoteService quoteService;

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;

	@Mock
	private CommonI18NService commonI18NService;

	@InjectMocks
	private QuoteProcessContextStrategy quoteProcessContextStrategy = new QuoteProcessContextStrategy();

	@Before
	public void setUp() throws Exception
	{
		final String quoteCode = "quote1";
		given(quoteProcessModel.getQuoteCode()).willReturn(quoteCode);
		given(quoteService.getCurrentQuoteForCode(quoteCode)).willReturn(quoteModel);
		given(quoteModel.getSite()).willReturn(baseSiteModel);
		given(quoteModel.getUser()).willReturn(customerModel);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Arrays.asList(quoteCurrency, userCurrency));
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Arrays.asList(customerLanguageModel));
	}

	@Test
	public void testGetCmsSite() throws Exception
	{
		assertSame(baseSiteModel, quoteProcessContextStrategy.getCmsSite(quoteProcessModel));
	}

	@Test
	public void testSetCurrencyFromQuoteIfPresent() throws Exception
	{
		given(quoteModel.getCurrency()).willReturn(quoteCurrency);

		quoteProcessContextStrategy.setCurrency(quoteProcessModel);

		verify(commonI18NService, atLeastOnce()).setCurrentCurrency(quoteCurrency);
	}

	@Test
	public void testSetCurrencyFromCustomerIfNotPresentInQuote() throws Exception
	{
		given(customerModel.getSessionCurrency()).willReturn(userCurrency);

		quoteProcessContextStrategy.setCurrency(quoteProcessModel);

		verify(commonI18NService, atLeastOnce()).setCurrentCurrency(userCurrency);
	}

	@Test
	public void testSetDefaultCurrencyIfNotPresentInQuoteorCustomer() throws Exception
	{
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(defaultCurrency);

		quoteProcessContextStrategy.setCurrency(quoteProcessModel);

		verify(commonI18NService, atLeastOnce()).setCurrentCurrency(defaultCurrency);
	}

	@Test
	public void testSetLanguageFromCustomer() throws Exception
	{
		given(customerModel.getSessionLanguage()).willReturn(customerLanguageModel);

		quoteProcessContextStrategy.setLanguage(quoteProcessModel);

		verify(commonI18NService, atLeastOnce()).setCurrentLanguage(customerLanguageModel);
	}

	@Test
	public void testSetDefaultLanguageIfNotPresentInCustomer() throws Exception
	{
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(defaultLanguage);

		quoteProcessContextStrategy.setLanguage(quoteProcessModel);

		verify(commonI18NService, atLeastOnce()).setCurrentLanguage(defaultLanguage);
	}

	@Test
	public void testGetOrderModel() throws Exception
	{
		final Optional<AbstractOrderModel> orderModelOptional = quoteProcessContextStrategy.getOrderModel(quoteProcessModel);

		assertSame(quoteModel, orderModelOptional.get());
	}
}
