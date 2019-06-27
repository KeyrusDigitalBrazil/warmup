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
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationQuoteIntegrationFacadeImplTest
{
	private static final String QUOTE_CODE = "q123";
	private static final String CONFIG_ID = "123";

	private ConfigurationQuoteIntegrationFacadeImpl classUnderTest;
	private ConfigurationAbstractOrderIntegrationHelperImpl configOrderHelper;
	private QuoteModel quoteModel;
	private List<AbstractOrderEntryModel> entryList;

	@Mock
	private QuoteService mockedQuoteService;
	@Mock
	private ProductConfigurationService mockedConfigService;
	@Mock
	private CPQConfigurableChecker mockedCpqConfigurableChecker;
	@Mock
	private ConfigPricing pricing;
	@Mock
	private ConfigModel configModel;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		configOrderHelper = new ConfigurationAbstractOrderIntegrationHelperImpl();
		configOrderHelper.setProductConfigurationService(mockedConfigService);
		configOrderHelper.setCpqConfigurableChecker(mockedCpqConfigurableChecker);
		configOrderHelper.setConfigPricing(pricing);
		configOrderHelper.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		configOrderHelper.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		configOrderHelper.setConfigurationVariantUtil(configurationVariantUtil);

		classUnderTest = new ConfigurationQuoteIntegrationFacadeImpl();
		classUnderTest.setQuoteService(mockedQuoteService);
		classUnderTest.setConfigurationAbstractOrderIntegrationHelper(configOrderHelper);

		quoteModel = new QuoteModel();
		entryList = new ArrayList<>();
		quoteModel.setCode(QUOTE_CODE);
		quoteModel.setEntries(entryList);


		given(mockedCpqConfigurableChecker.isCPQConfigurableProduct(any())).willReturn(true);
		given(mockedQuoteService.getCurrentQuoteForCode(QUOTE_CODE)).willReturn(quoteModel);
		given(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(Mockito.any())).willReturn(true);
		given(configurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntryForOneTimeAccess(any()))
				.willReturn(configModel);
		given(configModel.getId()).willReturn(CONFIG_ID);

	}

	protected void prepareQuoteItemWithConfig(final String configId, final int entryNumber)
	{
		final String extConfig = "myExtConfigForId" + configId;
		given(mockedConfigService.createConfigurationFromExternal(Mockito.any(KBKey.class), Mockito.eq(extConfig)))
				.willReturn(configModel);

		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(entryNumber));
		entry.setProduct(new ProductModel());
		entry.getProduct().setCode("p123");
		entry.setOrder(quoteModel);
		entryList.add(entry);
	}

	protected void prepareQuoteItemWithoutConfig(final int entryNumber)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(entryNumber));
		entry.setProduct(new ProductModel());
		entry.getProduct().setCode("p123");
		entry.setOrder(quoteModel);
		entryList.add(entry);
	}

	@Test
	public void testGetConfiguration()
	{
		prepareQuoteItemWithConfig(CONFIG_ID, 2);
		prepareQuoteItemWithoutConfig(5);

		final ConfigurationOverviewData ovData = classUnderTest.getConfiguration(QUOTE_CODE, 2);
		assertEquals(CONFIG_ID, ovData.getId());
		assertEquals("p123", ovData.getProductCode());
	}


	@Test
	public void testGetConfigurationEntryNotFound()
	{
		prepareQuoteItemWithoutConfig(5);
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(QUOTE_CODE);
		thrown.expectMessage(String.valueOf(2));
		classUnderTest.getConfiguration(QUOTE_CODE, 2);
	}

	@Test
	public void testFindQuoteNotFound()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("bla");
		classUnderTest.findQuote("bla");
	}

	@Test
	public void testFindQuote()
	{
		final QuoteModel result = classUnderTest.findQuote(QUOTE_CODE);
		assertNotNull(result);
		assertEquals(QUOTE_CODE, result.getCode());
	}

}
