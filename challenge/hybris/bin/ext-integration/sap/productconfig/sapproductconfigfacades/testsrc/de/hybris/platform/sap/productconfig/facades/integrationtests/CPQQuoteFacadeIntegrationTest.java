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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.LifecycleStrategiesTestChecker;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class CPQQuoteFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(CPQQuoteFacadeIntegrationTest.class);
	protected LifecycleStrategiesTestChecker lifecycleChecker;


	@Override
	protected void importCPQTestData() throws Exception
	{
		super.importCPQTestData();
		importCPQUserData();
		importCsv("/sapproductconfigfacades/test/sapProductConfig_quote_testData.impex", "utf-8");
	}

	@Before
	public void setUp() throws Exception
	{
		lifecycleChecker = selectStrategyTestChecker();
		prepareCPQData();
		/*
		 * Ensure we have the same user across the entire process
		 */
		realUserService.setCurrentUser(customerModel);
		login(USER_NAME, PASSWORD);
	}

	@Test
	public void testQuoteDataHasConfigurationData() throws CommerceCartModificationException, InvalidCredentialsException
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey);
		if (LOG.isDebugEnabled())
		{
			final StringBuilder sb = new StringBuilder().append("Configuration with ID ").append(configData.getConfigId())
					.append(" has been added to cart with item key ").append(cartItemKey);
			LOG.debug(sb.toString());
		}

		final QuoteData result = defaultQuoteFacade.initiateQuote();
		LOG.debug("Quote has been created from cart");
		final OrderEntryData entry = validateQuoteBasic(result);

		final List<ConfigurationInfoData> configQuoteData = entry.getConfigurationInfos();
		assertNotNull(configQuoteData);

		assertEquals(1, configQuoteData.size());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, configQuoteData.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, configQuoteData.get(0).getStatus());
		assertEquals("Simple Flag: Hide options", configQuoteData.get(0).getConfigurationLabel());
		assertEquals("Hide", configQuoteData.get(0).getConfigurationValue());
	}

	protected OrderEntryData validateQuoteBasic(final QuoteData result)
	{
		assertNotNull(result);
		assertNotNull(result.getEntries());
		assertEquals(1, result.getEntries().size());
		final OrderEntryData entry = result.getEntries().get(0);

		assertNotNull(entry.getItemPK());
		assertTrue(entry.isConfigurationAttached());

		assertFalse(entry.isConfigurationConsistent());
		assertEquals(2, entry.getConfigurationErrorCount());

		assertNotNull(entry.getStatusSummaryMap());
		assertEquals(1, entry.getStatusSummaryMap().size());
		assertEquals(Integer.valueOf(2), entry.getStatusSummaryMap().get(ProductInfoStatus.ERROR));
		return entry;
	}

	@Test
	public void testQuoteSessionArtifacts() throws CommerceCartModificationException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry. We expect to have session artifacts belonging to our cart entry
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartEntryKey = cpqCartFacade.addConfigurationToCart(configData);
		checkSessionArtifactsPresent(cartEntryKey);

		/*
		 * Step 2: Create a quote from cart. Now the session artifacts for the previous cart entry are gone as we released
		 * them
		 */
		final QuoteData quote = defaultQuoteFacade.initiateQuote();
		assertNotNull(quote);

		final List<OrderEntryData> quoteEntries = quote.getEntries();
		assertEquals(1, quoteEntries.size());
		final String quoteEntryKey = quoteEntries.get(0).getItemPK();

		checkSessionArtifactsNone(cartEntryKey);

		/*
		 * Step 3: Now we start quotation edit process, quotation is not submitted yet. We get a new session cart entry,
		 * based on its corresponding quote entry, for which we expect to have session artifacts
		 */
		defaultQuoteFacade.enableQuoteEdit(quote.getCode());
		final List<OrderEntryData> cartEntriesQuoteEdit = cartFacade.getSessionCart().getEntries();
		assertEquals(1, cartEntriesQuoteEdit.size());
		final String cartEntryQuoteEdit = cartEntriesQuoteEdit.get(0).getItemPK();
		assertFalse(cartEntryQuoteEdit.equals(quoteEntryKey));
		assertFalse(cartEntryQuoteEdit.equals(cartEntryKey));

		checkSessionArtifactsPresent(cartEntryQuoteEdit);

		/*
		 * Step 4: Quotation is submitted. All session artifacts must be gone
		 */
		// disable threshold check, only executed if quote user equals cart user
		cartService.getSessionCart().setUser(realUserService.getAnonymousUser());
		defaultQuoteFacade.submitQuote(quote.getCode());
		checkSessionArtifactsNone(cartEntryKey);
		checkSessionArtifactsNone(quoteEntryKey);
		checkSessionArtifactsNone(cartEntryQuoteEdit);
	}

	@Test
	public void testQuoteSessionArtifactsDraftHandling() throws CommerceCartModificationException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry. We expect to have session artifacts belonging to our cart entry
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartEntryKey = cpqCartFacade.addConfigurationToCart(configData);

		/*
		 * Step 2: Configure from cart, create draft
		 */
		final String cartItemKey = cartFacade.getSessionCart().getEntries().get(0).getItemPK().toString();
		cpqCartFacade.configureCartItem(cartItemKey);
		lifecycleChecker.checkNumberOfConfigsPersisted("We expect 2 configurations at this point as we created a draft. ", 2);


		/*
		 * Step 3: Create a quote from cart. The draft attached to the previous cart must be gone
		 */
		int numExpected = isPersistentLifecycle() ? 1 : 0;

		final QuoteData quote = defaultQuoteFacade.initiateQuote();
		lifecycleChecker.checkNumberOfConfigsPersisted("We expect only one configuration when we converted the cart into quote. ",
				numExpected);

		/*
		 * Step 3: Now we start quotation edit process, quotation is not submitted yet.
		 */
		defaultQuoteFacade.enableQuoteEdit(quote.getCode());

		/*
		 * Step 4: Create a new draft for the quote edit cart
		 */
		numExpected = isPersistentLifecycle() ? 3 : 2;
		final String cartEditQuoteItemKey = cartFacade.getSessionCart().getEntries().get(0).getItemPK().toString();
		cpqCartFacade.configureCartItem(cartEditQuoteItemKey);
		lifecycleChecker.checkNumberOfConfigsPersisted(
				"We expect 3 (2) configurations at this point as we created a draft for the quote entry. This means we need configurations for quote, for quote cart and for draft. (only two raft with default session handling - darft/quoteCrat) ",
				numExpected);

		/*
		 * Step 5: Quotation is submitted. All session artifacts must be gone
		 */
		numExpected = isPersistentLifecycle() ? 1 : 0;
		// disable threshold check, only executed if quote user equals cart user
		cartService.getSessionCart().setUser(realUserService.getAnonymousUser());
		defaultQuoteFacade.submitQuote(quote.getCode());
		lifecycleChecker.checkNumberOfConfigsPersisted("We expect only one configuration after quote submit", numExpected);
	}

	@Test
	public void testConfigQuoteIntegrationFacade() throws CommerceCartModificationException, InvalidCredentialsException
	{
		/*
		 * Step 1: Create a cart containing one entry.
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);

		/*
		 * Step 2: Create a quote from cart.
		 */
		final QuoteData quote = defaultQuoteFacade.initiateQuote();

		/*
		 * Step 3: Get the ConfigurationOverviewData from ConfigurationQuoteIntegrationFacade.
		 */
		final String quoteCode = quote.getCode();
		final int entryNumber = quote.getEntries().get(0).getEntryNumber().intValue();

		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, entryNumber);
		assertNotNull(configOverview);
		assertEquals(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), configOverview.getProductCode());
	}

	protected void checkSessionArtifactsNone(final String cartEntryKey)
	{
		try
		{
			final String configId = cpqSessionAccessFacade.getConfigIdForCartEntry(cartEntryKey);
			assertTrue(StringUtils.isEmpty(configId));
		}
		catch (final ModelNotFoundException ex)
		{
			// valid - cartEntry already deleted
		}
	}

	protected void checkSessionArtifactsPresent(final String cartEntryKey)
	{
		final String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartEntryKey);
		assertFalse("We expect a config ID for cart entry", StringUtils.isEmpty(configId));
		final String entryKeyFromSession = cpqAbstractOrderEntryLinkStrategy.getCartEntryForConfigId(configId);
		assertEquals("Cart entry keys must match", entryKeyFromSession, cartEntryKey);
		final ConfigModel configModel = cpqService.retrieveConfigurationModel(configId);
		assertNotNull("We expect to get config model from product config service", configModel);
	}

	protected String getCartEntryKey(final CartData sessionCart)
	{
		assertEquals(1, sessionCart.getEntries().size());
		final OrderEntryData cartEntry = sessionCart.getEntries().get(0);
		final String cartKey = cartEntry.getItemPK();
		return cartKey;
	}

	@Test
	public void testQuoteDataHasNoConfigurationData() throws CommerceCartModificationException, InvalidCredentialsException
	{
		// trigger add to cart without going through configuration facade "dark add to cart"
		final CartModificationData cartModificationData = cartFacade.addToCart(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), 1);
		assertNotNull(cartModificationData);

		final QuoteData result = defaultQuoteFacade.initiateQuote();
		final OrderEntryData entry = validateQuoteBasic(result);

		final List<ConfigurationInfoData> configQuoteData = entry.getConfigurationInfos();
		assertNotNull(configQuoteData);
		assertEquals(1, configQuoteData.size());

		final ConfigurationInfoData info = configQuoteData.get(0);
		assertNull(info.getConfigurationLabel());
		assertNull(info.getConfigurationValue());
		assertNotNull(info.getConfiguratorType());
	}

}
