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
package de.hybris.platform.commerceservices.order.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCommerceQuoteDao}
 */
@IntegrationTest
public class DefaultCommerceQuoteDaoTest extends ServicelayerTest
{
	private static final String QUOTE_CODE = "quote0";
	private static final String TEST_BASESTORE_UID = "testStore";
	private static final String TEST_CUSTOMER_UID = "quotecustomer";

	@Resource
	private DefaultCommerceQuoteDao defaultCommerceQuoteDao;

	@Resource
	private UserService userService;

	@Resource
	private BaseStoreService baseStoreService;

	private BaseStoreModel baseStore;
	private CustomerModel customer;

	@Before
	public void setUp() throws Exception
	{
		// importing test csv
		importCsv("/commerceservices/test/testQuotes.impex", "utf-8");
		baseStore = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
		customer = userService.getUserForUID(TEST_CUSTOMER_UID, CustomerModel.class);
	}

	@Test
	public void shouldFindQuotesByCustomerAndStore()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);
		final SearchPageData<QuoteModel> quotes = defaultCommerceQuoteDao.findQuotesByCustomerAndStore(customer, baseStore,
				ceateSamplePageableData(), quoteStates);
		assertNotNull("quotes is null", quotes);
		assertNotNull("quotes result is null", quotes.getResults());
		assertEquals(3, quotes.getResults().size());
		assertEquals("quote2", quotes.getResults().get(0).getCode());
		assertEquals("quote1", quotes.getResults().get(1).getCode());
		assertEquals(QUOTE_CODE, quotes.getResults().get(2).getCode());
	}

	@Test
	public void shouldNotFindQuotesSinceNoQuoteWithWrongState()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_ACCEPTED);
		quoteStates.add(QuoteState.BUYER_ORDERED);
		final SearchPageData<QuoteModel> quotes = defaultCommerceQuoteDao.findQuotesByCustomerAndStore(customer, baseStore,
				ceateSamplePageableData(), quoteStates);
		assertNotNull("quotes is null", quotes);
		assertNotNull("quotes result is null", quotes.getResults());
		assertEquals(0, quotes.getResults().size());
	}

	@Test
	public void shouldNotFindQuotesByNoQuoteCustomer()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		final SearchPageData<QuoteModel> quotes = defaultCommerceQuoteDao.findQuotesByCustomerAndStore(
				userService.getUserForUID("noquotecustomer", CustomerModel.class), baseStore, ceateSamplePageableData(), quoteStates);
		assertNotNull("quotes is null", quotes);
		assertNotNull("quotes result is null", quotes.getResults());
		assertEquals(0, quotes.getResults().size());
	}

	@Test
	public void shouldNotFindQuotesByNoQuoteStore()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);
		final SearchPageData<QuoteModel> quotes = defaultCommerceQuoteDao.findQuotesByCustomerAndStore(customer,
				baseStoreService.getBaseStoreForUid("noQuoteTestStore"), ceateSamplePageableData(), quoteStates);
		assertNotNull("quotes is null", quotes);
		assertNotNull("quotes result is null", quotes.getResults());
		assertEquals(0, quotes.getResults().size());
	}

	@Test
	public void shouldGetQuotesCountByCustomerAndStore()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		final int quoteCount = defaultCommerceQuoteDao.getQuotesCountForCustomerAndStore(customer, baseStore, quoteStates)
				.intValue();

		assertEquals("Should get 3 quotes", 3, quoteCount);
	}

	@Test
	public void shouldGetZeroQuoteCountSinceNoQuoteWithWrongState()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_ACCEPTED);
		quoteStates.add(QuoteState.BUYER_ORDERED);

		final int quoteCount = defaultCommerceQuoteDao.getQuotesCountForCustomerAndStore(customer, baseStore, quoteStates)
				.intValue();

		assertEquals("Should get 0 quotes", 0, quoteCount);
	}

	@Test
	public void shouldGetZeroQuoteCountForNoQuoteCustomer()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		final int quoteCount = defaultCommerceQuoteDao.getQuotesCountForCustomerAndStore(
				userService.getUserForUID("noquotecustomer", CustomerModel.class), baseStore, quoteStates).intValue();

		assertEquals("Should get 0 quotes", 0, quoteCount);
	}

	@Test
	public void shouldGetZeroQuoteCountForNoQuoteStore()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		final int quoteCount = defaultCommerceQuoteDao
				.getQuotesCountForCustomerAndStore(customer, baseStoreService.getBaseStoreForUid("noQuoteTestStore"), quoteStates)
				.intValue();

		assertEquals("Should get 0 quotes", 0, quoteCount);
	}

	@Test
	public void shouldFindQuoteByCode()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		final QuoteModel quote = defaultCommerceQuoteDao.findUniqueQuoteByCodeAndCustomerAndStore(customer, baseStore, QUOTE_CODE,
				quoteStates);
		assertNotNull("quotes is null", quote);
		assertEquals(QuoteState.BUYER_OFFER, quote.getState());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotFindQuoteByCode()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);
		defaultCommerceQuoteDao.findUniqueQuoteByCodeAndCustomerAndStore(customer, baseStore, "notExistQuote", quoteStates);
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotFindQuoteByCodeWithWrongState()
	{
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_ACCEPTED);
		quoteStates.add(QuoteState.BUYER_ORDERED);
		defaultCommerceQuoteDao.findUniqueQuoteByCodeAndCustomerAndStore(customer, baseStore, QUOTE_CODE, quoteStates);
	}

	protected PageableData ceateSamplePageableData()
	{
		final PageableData pd = new PageableData();
		pd.setCurrentPage(0);
		pd.setPageSize(10);
		return pd;
	}
}
