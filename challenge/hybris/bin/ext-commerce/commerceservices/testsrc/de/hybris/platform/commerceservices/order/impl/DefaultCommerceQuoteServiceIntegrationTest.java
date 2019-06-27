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

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.QUOTE_REQUEST_INITIATION_THRESHOLD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceQuoteAssignmentException;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCommerceQuoteServiceIntegrationTest extends ServicelayerTest
{

	@Resource
	private QuoteService quoteService;
	@Resource
	private UserService userService;
	@Resource
	private CommerceQuoteService commerceQuoteService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private CartService cartService;

	private UserModel testcustomer1, testcustomer2;

	private final Map<String, String> originalConfigs = new HashMap<String, String>();

	protected static void createCommerceQuoteData() throws ImpExException
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		// importing test csv
		importCsv("/commerceservices/test/commerceQuoteTestData.csv", "utf-8");
	}

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createCommerceQuoteData();
		testcustomer1 = userService.getUserForUID("testcustomer1");
		testcustomer2 = userService.getUserForUID("testcustomer2");
		importCsv("/commerceservices/test/testQuotes.impex", "utf-8");

		originalConfigs.put(QUOTE_REQUEST_INITIATION_THRESHOLD, Config.getParameter(QUOTE_REQUEST_INITIATION_THRESHOLD));
		originalConfigs.put(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR",
				Config.getParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR"));
	}

	@After
	public void tearDown()
	{
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD, originalConfigs.get(QUOTE_REQUEST_INITIATION_THRESHOLD));
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR",
				originalConfigs.get(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAssignQuoteToUserForNullQuote()
	{
		commerceQuoteService.assignQuoteToUser(null, testcustomer1, testcustomer1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAssignQuoteToUserForNullAssigne()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.assignQuoteToUser(quote, null, testcustomer1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAssignQuoteToUserForNullAssigner()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.assignQuoteToUser(quote, testcustomer1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUnassignQuoteForNullQuote()
	{
		commerceQuoteService.unassignQuote(null, testcustomer1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUnassignQuoteForNullAssigner()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.unassignQuote(quote, null);
	}

	@Test
	public void shouldAssignQuoteToUser()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.assignQuoteToUser(quote, testcustomer1, testcustomer1);

		assertNotNull("Quote should be assigned", quote.getAssignee());
		assertEquals("Quote should be assigned to testcustomer1", "testcustomer1", quote.getAssignee().getUid());
	}

	@Test(expected = CommerceQuoteAssignmentException.class)
	public void shouldThrowExceptionWhenAssignerIsNotAssigneeForQuoteAssignment()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.assignQuoteToUser(quote, testcustomer1, testcustomer2);
	}

	@Test
	public void shouldKeepAssignmentWhenAssigneeIsAlreadySetForQuoteAssignment()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		quote.setAssignee(testcustomer1);
		commerceQuoteService.assignQuoteToUser(quote, testcustomer1, testcustomer1);

		assertNotNull(quote.getAssignee());
		assertEquals("Quote should be assigned to testcustomer1", "testcustomer1", quote.getAssignee().getUid());
	}

	@Test(expected = CommerceQuoteAssignmentException.class)
	public void shouldThrowExceptionWhenQuoteIsAssignedAlreadyToDifferentUser()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		quote.setAssignee(testcustomer2);
		commerceQuoteService.assignQuoteToUser(quote, testcustomer1, testcustomer2);
	}

	@Test
	public void shouldUnassignUserFromQuote()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		quote.setAssignee(testcustomer1);
		commerceQuoteService.unassignQuote(quote, testcustomer1);

		assertNull("Quote should be unassigned", quote.getAssignee());
	}

	@Test(expected = CommerceQuoteAssignmentException.class)
	public void shouldThrowExceptionWhenAssignerIsNotAssigneeForQuoteUnassign()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		quote.setAssignee(testcustomer1);

		commerceQuoteService.unassignQuote(quote, testcustomer2);
	}

	@Test
	public void shouldKeepNullAssigneeWhenAssigneeIsNotSetForQuoteUnassign()
	{
		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(4));
		commerceQuoteService.unassignQuote(quote, testcustomer1);

		assertNull("Quote should be unassigned", quote.getAssignee());
	}

	@Test
	public void shoudGetDefaultQuoteRequestThreshold()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		final UserModel user = sessionCart.getUser();

		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(1));
		// reset config
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD, "123");
		final double threshold = commerceQuoteService.getQuoteRequestThreshold(quote, user, cartService.getSessionCart());
		// should get default value, given no site/currency value was defined.
		assertEquals(123, threshold, 0.1);
	}

	@Test
	public void shoudGetSiteQuoteRequestThresholdIfAvailable()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		final UserModel user = sessionCart.getUser();

		final QuoteModel quote = quoteService.getQuoteForCodeAndVersion("testQuote0", Integer.valueOf(1));
		quote.setSite(baseSiteService.getBaseSiteForUID("testSite"));
		// set config for site/currency
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR", "321");
		final double threshold = commerceQuoteService.getQuoteRequestThreshold(quote, user, cartService.getSessionCart());
		assertEquals(321, threshold, 0.1);
	}

}
