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
package de.hybris.platform.commerceservices.strategies.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteSellerApproverAutoApprovalStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for {@link DefaultQuoteSellerApproverAutoApprovalStrategy}
 */
@UnitTest
public class DefaultQuoteSellerApproverAutoApprovalStrategyTest
{
	@InjectMocks
	private final DefaultQuoteSellerApproverAutoApprovalStrategy defaultQuoteAutoApprovalStrategy = new DefaultQuoteSellerApproverAutoApprovalStrategy();
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private QuoteModel quoteModel;

	private static final String THRESHOLD_WITH_CURRENCY = "commerceservices.quote.seller.auto.approval.threshold.powertools.USD";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultQuoteAutoApprovalStrategy.setConfigurationService(configurationService);
		given(configurationService.getConfiguration()).willReturn(configuration);

		quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("1234");
		given(quoteModel.getState()).willReturn(QuoteState.SELLER_SUBMITTED);

		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		given(baseSiteModel.getUid()).willReturn("powertools");
		given(quoteModel.getSite()).willReturn(baseSiteModel);

		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(quoteModel.getCurrency()).willReturn(currencyModel);
	}

	@Test
	public void shouldAutoApproveByGlobalThreshold()
	{
		given(Double.valueOf(configuration.getDouble(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, 0.0d)))
				.willReturn(Double.valueOf(75000d));

		given(Double.valueOf(configuration.getDouble(THRESHOLD_WITH_CURRENCY, 75000d))).willReturn(Double.valueOf(75000d));

		given(quoteModel.getSubtotal()).willReturn(Double.valueOf(74000d));
		assertTrue(defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));
	}

	@Test
	public void shouldNotAutoApproveByGlobalThreshold()
	{
		given(Double.valueOf(configuration.getDouble(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, 0.0d)))
				.willReturn(Double.valueOf(75000d));

		given(Double.valueOf(configuration.getDouble(THRESHOLD_WITH_CURRENCY, 75000d))).willReturn(Double.valueOf(75000d));

		given(quoteModel.getSubtotal()).willReturn(Double.valueOf(76000d));
		assertFalse(defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));
	}

	@Test
	public void shouldAutoApproveBySiteCurrencyThreshold()
	{
		given(Double.valueOf(configuration.getDouble(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, 0.0d)))
				.willReturn(Double.valueOf(75000d));

		given(Double.valueOf(configuration.getDouble(THRESHOLD_WITH_CURRENCY, 75000d))).willReturn(Double.valueOf(1000d));

		given(quoteModel.getSubtotal()).willReturn(Double.valueOf(999d));
		assertTrue(defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));
	}

	@Test
	public void shouldNotAutoApproveBySiteCurrencyThreshold()
	{
		given(Double.valueOf(configuration.getDouble(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, 0.0d)))
				.willReturn(Double.valueOf(75000d));

		given(Double.valueOf(configuration.getDouble(THRESHOLD_WITH_CURRENCY, 75000d))).willReturn(Double.valueOf(1000d));

		given(quoteModel.getSubtotal()).willReturn(Double.valueOf(1100d));
		assertFalse(defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));
	}

	@Test
	public void testShouldAutoApproveTheQuote()
	{
		final double[][] testMatrix = new double[][]
		{
				{ 1_000d, 999d, 999.99d, 1_000.01d, 1_001d },
				{ 5_000d, 4_999d, 4_999.99d, 5_000.01d, 5_001d },
				{ 10_000d, 9_999d, 9_999.99d, 10_000.01d, 10_001d },
				{ 75_000d, 74_999d, 74_999.99d, 75_000.01d, 75_001d },
				{ 500_000d, 499_999d, 499_999.99d, 500_000.01d, 500_001d },
				{ 750_000d, 749_999d, 749_999.99d, 750_000.01d, 750_001d },
				{ 1_000_000d, 999_999d, 999_999.99d, 1_000_000.01d, 1_000_001d },
				{ 10_000_000d, 9_999_999d, 9_999_999.99d, 10_000_000.01d, 10_000_001d },
				{ 100_000_000d, 99_999_999d, 99_999_999.99d, 100_000_000.01d, 100_000_001d } };

		for (final double[] testRow : testMatrix)
		{
			given(Double.valueOf(configuration.getDouble(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, 0.0d)))
					.willReturn(Double.valueOf(0.0d));

			given(Double.valueOf(configuration.getDouble(THRESHOLD_WITH_CURRENCY, 0.0d))).willReturn(Double.valueOf(testRow[0]));

			given(quoteModel.getSubtotal()).willReturn(Double.valueOf(testRow[1]));
			assertTrue(String.format("Should approve lower value: %.2f", Double.valueOf(testRow[1])),
					defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));

			given(quoteModel.getSubtotal()).willReturn(Double.valueOf(testRow[2]));
			assertTrue(String.format("Should approve lower value (decimal): %.2f", Double.valueOf(testRow[2])),
					defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));

			given(quoteModel.getSubtotal()).willReturn(Double.valueOf(testRow[0]));
			assertFalse(String.format("Should not approve value equal to threshold: %.2f", Double.valueOf(testRow[0])),
					defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));

			given(quoteModel.getSubtotal()).willReturn(Double.valueOf(testRow[3]));
			assertFalse(String.format("Should not approve value greater than threshold (decimal): %.2f", Double.valueOf(testRow[3])),
					defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));

			given(quoteModel.getSubtotal()).willReturn(Double.valueOf(testRow[4]));
			assertFalse(String.format("Should not approve value greater than threshold: %.2f", Double.valueOf(testRow[4])),
					defaultQuoteAutoApprovalStrategy.shouldAutoApproveQuote(quoteModel));
		}
	}
}
