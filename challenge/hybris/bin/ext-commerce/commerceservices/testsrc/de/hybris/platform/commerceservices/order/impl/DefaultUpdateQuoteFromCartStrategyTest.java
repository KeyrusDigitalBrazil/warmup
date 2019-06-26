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
package de.hybris.platform.commerceservices.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test of {@link DefaultUpdateQuoteFromCartStrategy}. This needs to be an integration test as
 * {@link DefaultUpdateQuoteFromCartStrategy#updateQuoteFromCart(CartModel)} executes code inside a {@link Transaction}.
 */
@IntegrationTest
public class DefaultUpdateQuoteFromCartStrategyTest extends ServicelayerTransactionalTest
{
	private static final String UPDATED_NAME = "updatedName";
	private static final String UPDATE_DESCRIPTION = "updateDescription";
	private static final String TEST_QUOTE_CODE = "testQuote0";
	private static final Integer TEST_QUOTE_VERSION = Integer.valueOf(1);
	private static final QuoteState TEST_QUOTE_STATE = QuoteState.CREATED;
	private static final Double estimatedTotal = Double.valueOf(25000.89);

	@Resource
	private DefaultUpdateQuoteFromCartStrategy defaultUpdateQuoteFromCartStrategy;

	@Resource
	private ModelService modelService;

	@Resource
	private CartService cartService;

	@Resource
	private QuoteService quoteService;

	private CartModel sessionCart;
	private QuoteModel testQuote;
	private Date testExpirationTime;

	protected static void createQuoteData() throws ImpExException
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		// importing test csv
		importCsv("/platformservices/test/quoteTestData.csv", "windows-1252");
	}

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createQuoteData();

		testQuote = quoteService.getQuoteForCodeAndVersion(TEST_QUOTE_CODE, TEST_QUOTE_VERSION);
		testExpirationTime = new Date();

		sessionCart = cartService.getSessionCart();
		sessionCart.setQuoteReference(testQuote);
		sessionCart.setName(UPDATED_NAME);
		sessionCart.setDescription(UPDATE_DESCRIPTION);
		sessionCart.setExpirationTime(testExpirationTime);
		testQuote.setPreviousEstimatedTotal(estimatedTotal);

		modelService.saveAll(testQuote, sessionCart);
	}

	@Test
	public void shouldUpdateQuoteFromCart() throws InvalidCartException
	{
		final QuoteModel updatedQuote = defaultUpdateQuoteFromCartStrategy.updateQuoteFromCart(sessionCart);

		assertNotNull("Updated quote is null", updatedQuote);
		assertEquals("Unexpected code", TEST_QUOTE_CODE, updatedQuote.getCode());
		assertEquals("Unexpected version", TEST_QUOTE_VERSION, updatedQuote.getVersion());
		assertEquals("Unexpected state", TEST_QUOTE_STATE, updatedQuote.getState());
		assertEquals("Unexpected name", UPDATED_NAME, updatedQuote.getName());
		assertEquals("Unexpected description", UPDATE_DESCRIPTION, updatedQuote.getDescription());
		assertEquals("Unexpected expirationTime", testExpirationTime, updatedQuote.getExpirationTime());
		assertEquals("Unexpected quote.cartReference", sessionCart, updatedQuote.getCartReference());
		assertEquals("Unexpected cart.quoteReference", updatedQuote, sessionCart.getQuoteReference());
		assertEquals("Previous estimated total is null", estimatedTotal, updatedQuote.getPreviousEstimatedTotal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteFromCartIfCartIsNull() throws InvalidCartException
	{
		defaultUpdateQuoteFromCartStrategy.updateQuoteFromCart(null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotUpdateQuoteFromCartIfCartIsNotReferencedFromQuote() throws InvalidCartException
	{
		defaultUpdateQuoteFromCartStrategy.updateQuoteFromCart(new CartModel());
	}
}
