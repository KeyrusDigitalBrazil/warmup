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
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCommerceOrderDao}
 */
@IntegrationTest
public class DefaultCommerceOrderDaoTest extends BaseCommerceBaseTest
{

	private static final String TEST_QUOTE_CODE_1 = "testQuote1";
	private static final String TEST_QUOTE_CODE_2 = "testQuote2";
	private static final String TEST_QUOTE_CODE_3 = "testQuote3";

	@Resource
	private DefaultCommerceOrderDao defaultCommerceOrderDao;

	@Resource
	private UserService userService;

	@Resource
	private QuoteService quoteService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		// importing test csv
		importCsv("/commerceservices/test/testQuoteOrders.impex", "utf-8");
		userService.setCurrentUser(userService.getUserForUID("orderhistoryuser@test.com"));
	}

	@Test
	public void shouldFindOrderForQuote()
	{
		final OrderModel order = defaultCommerceOrderDao.findOrderByQuote(quoteService.getCurrentQuoteForCode(TEST_QUOTE_CODE_1));

		assertNotNull("order should not be null", order);
		assertNotNull("quote reference for order should not be null", order.getQuoteReference());
		assertEquals("quote code doesnt match for passed quote & order.quoteReference", TEST_QUOTE_CODE_1,
				order.getQuoteReference().getCode());
	}

	@Test
	public void shouldNotFindOrderWhenSameQuoteHasReferenceToMultipleOrders()
	{
		final OrderModel order = defaultCommerceOrderDao.findOrderByQuote(quoteService.getCurrentQuoteForCode(TEST_QUOTE_CODE_2));

		assertNull("order should be null", order);
	}

	@Test
	public void shouldNotFindOrderForQuoteWithNoQuoteReference()
	{
		final OrderModel order = defaultCommerceOrderDao.findOrderByQuote(quoteService.getCurrentQuoteForCode(TEST_QUOTE_CODE_3));

		assertNull("order should be null", order);
	}

}
