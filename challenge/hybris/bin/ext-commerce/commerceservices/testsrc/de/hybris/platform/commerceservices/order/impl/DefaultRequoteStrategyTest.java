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
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test of {@link DefaultRequoteStrategy}.
 */
@IntegrationTest
public class DefaultRequoteStrategyTest extends ServicelayerTransactionalTest
{
	private static final String TEST_QUOTE_CODE = "testQuote1";
	private static final Integer TEST_QUOTE_VERSION = Integer.valueOf(3);

	@Resource
	private DefaultRequoteStrategy defaultRequoteStrategy;

	@Resource
	private QuoteService quoteService;

	private QuoteModel testQuote;

	protected static void createQuoteData() throws ImpExException
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		// importing test csv
		importCsv("/commerceservices/test/quoteTestData.csv", "windows-1252");
	}

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createQuoteData();

		testQuote = quoteService.getQuoteForCodeAndVersion(TEST_QUOTE_CODE, TEST_QUOTE_VERSION);
	}

	@Test
	public void shouldRequote() throws InvalidCartException
	{
		final QuoteModel newQuote = defaultRequoteStrategy.requote(testQuote);

		assertNotNull("New quote is not null", newQuote);
		assertNull("New quote name is null", newQuote.getName());
		assertNull("New quote expire time is null", newQuote.getExpirationTime());
		assertNull("New quote comment is null", newQuote.getComments());
		assertEquals("New quote discount is cleared", 0, newQuote.getGlobalDiscountValues().size());
		assertEquals("New quote version is 1", Integer.valueOf(1), newQuote.getVersion());
		assertEquals("New quote state is buyer_draft", QuoteState.BUYER_DRAFT, newQuote.getState());
		assertNull("New quote's assign is null", newQuote.getAssignee());
		assertNull("New quote's cart reference is null", newQuote.getCartReference());
		assertNull("New quote's generated notification is null", newQuote.getGeneratedNotifications());
		assertNull("New quote's previous estimated total is null", newQuote.getPreviousEstimatedTotal());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRequoteIfExistingQuoteIsNull() throws InvalidCartException
	{
		defaultRequoteStrategy.requote(null);
	}
}
