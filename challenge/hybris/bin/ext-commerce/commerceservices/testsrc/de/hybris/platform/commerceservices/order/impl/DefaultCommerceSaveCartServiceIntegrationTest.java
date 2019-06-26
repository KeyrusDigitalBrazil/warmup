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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for functionality for retrieving saved carts.
 *
 * @See de.hybris.platform.commerceservices.order.CommerceSaveCartService#getSavedCartsForSiteAndUser
 */
@IntegrationTest
public class DefaultCommerceSaveCartServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceSaveCartServiceIntegrationTest.class);

	private static final String TEST_BASESITE1_UID = "testSite";
	private static final String TEST_BASESITE2_UID = "testSite2";

	private static final String USER1 = "abrode";
	private static final String USER2 = "ahertz";

	protected static final String SORT_CODE_BY_DATE_MODIFIED = "byDateModified";
	protected static final String SORT_CODE_BY_DATE_SAVED = "byDateSaved";
	protected static final String SORT_CODE_BY_NAME = "byName";
	protected static final String SORT_CODE_BY_CODE = "byCode";
	protected static final String SORT_CODE_BY_TOTAL = "byTotal";

	protected static final String CART_NAME_PREFIX_A = "A";
	protected static final String CART_NAME_PREFIX_B = "BBBBB";

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CommerceSaveCartService commerceSaveCartService;

	@Resource
	private CartService cartService;

	@Resource
	private UserService userService;

	@Resource
	private CartFactory cartFactory;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for commerce cart ..");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID), false);

		LOG.info("Finished data for commerce cart " + (System.currentTimeMillis() - startTime) + "ms");
	}


	/**
	 * Test loads used and his cart, saves it, then sets another basesite and saves session cart.
	 *
	 * @throws CommerceSaveCartException
	 */
	@Test
	public void testGetSavedCarts() throws CommerceSaveCartException
	{
		final UserModel user = userService.getUserForUID(USER1);

		// save cart for user1 on baseSite1
		CommerceSaveCartParameter commerceSaveCartParameter = generateSaveCartParameter(USER1, TEST_BASESITE1_UID, false);
		commerceSaveCartService.saveCart(commerceSaveCartParameter);

		final PageableData pd = new PageableData();
		pd.setCurrentPage(0);
		pd.setPageSize(10);
		final SearchPageData<CartModel> savedFirst = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, null, user, null);
		assertEquals(savedFirst.getResults().size(), 1);

		// switching to baseSite2 and updating session cart
		commerceSaveCartParameter = generateSaveCartParameter(USER1, TEST_BASESITE2_UID, true);
		commerceSaveCartService.saveCart(commerceSaveCartParameter);

		final SearchPageData<CartModel> savedSecond = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, null, user, null);
		assertEquals(savedSecond.getResults().size(), 2);

		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		final SearchPageData<CartModel> savedCartsForSiteAndUserFirst = commerceSaveCartService.getSavedCartsForSiteAndUser(pd,
				baseSite1, user, null);
		assertEquals(savedCartsForSiteAndUserFirst.getResults().size(), 1);

		final BaseSiteModel baseSite2 = baseSiteService.getBaseSiteForUID(TEST_BASESITE2_UID);
		final SearchPageData<CartModel> savedCartsForSiteAndUserSecond = commerceSaveCartService.getSavedCartsForSiteAndUser(pd,
				baseSite2, user, null);
		assertEquals(savedCartsForSiteAndUserSecond.getResults().size(), 1);

		// they are not the same
		assertFalse(savedCartsForSiteAndUserSecond.getResults().get(0).getPk()
				.equals(savedCartsForSiteAndUserFirst.getResults().get(0).getPk()));
	}

	@Test
	public void testGetSavedCartsCount() throws CommerceSaveCartException
	{
		final UserModel user = userService.getUserForUID(USER2);

		// save cart for user2 on baseSite1
		CommerceSaveCartParameter commerceSaveCartParameter = generateSaveCartParameter(USER2, TEST_BASESITE1_UID, false);
		commerceSaveCartService.saveCart(commerceSaveCartParameter);
		assertEquals(commerceSaveCartService.getSavedCartsCountForSiteAndUser(null, user).intValue(), 1);

		// switching to baseSite2 and updating session cart
		commerceSaveCartParameter = generateSaveCartParameter(USER2, TEST_BASESITE2_UID, true);
		commerceSaveCartService.saveCart(commerceSaveCartParameter);
		assertEquals(commerceSaveCartService.getSavedCartsCountForSiteAndUser(null, user).intValue(), 2);

		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		assertEquals(commerceSaveCartService.getSavedCartsCountForSiteAndUser(baseSite1, user).intValue(), 1);

		final BaseSiteModel baseSite2 = baseSiteService.getBaseSiteForUID(TEST_BASESITE2_UID);
		assertEquals(commerceSaveCartService.getSavedCartsCountForSiteAndUser(baseSite2, user).intValue(), 1);

		// anonymous user
		assertEquals(commerceSaveCartService.getSavedCartsCountForSiteAndUser(null, userService.getAnonymousUser()).intValue(), 0);
	}

	private CommerceSaveCartParameter generateSaveCartParameter(final String strUser, final String strBaseSite,
			final boolean saveSessionCart)
	{
		final CommerceSaveCartParameter commerceSaveCartParameter = new CommerceSaveCartParameter();
		commerceSaveCartParameter.setName("name");
		commerceSaveCartParameter.setDescription("description");

		final UserModel user = userService.getUserForUID(strUser);
		if (saveSessionCart)
		{
			baseSiteService.setCurrentBaseSite(strBaseSite, false);
			userService.setCurrentUser(user);
			commerceSaveCartParameter.setCart(cartService.getSessionCart());

			return commerceSaveCartParameter;
		}

		if (user.getCarts() != null && user.getCarts().size() > 0)
		{
			final CartModel cartModel = user.getCarts().iterator().next();
			cartModel.setSite(baseSiteService.getBaseSiteForUID(strBaseSite));
			commerceSaveCartParameter.setCart(cartModel);
		}

		return commerceSaveCartParameter;

	}

	/**
	 * Test saves carts and checks that sorting criteria is handled correctly.
	 *
	 * @throws CommerceSaveCartException
	 * @throws InterruptedException
	 */
	@Test
	public void testGetSavedCardsSortingNoBaseSiteNoOrderStatus() throws CommerceSaveCartException, InterruptedException
	{
		final UserModel dejol = userService.getUserForUID("dejol");
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		userService.setCurrentUser(dejol);

		createSavedCarts(baseSite, dejol);

		sortAndTest(null, dejol, null);
	}

	/**
	 * Test saves carts and checks that sorting criteria is handled correctly.
	 *
	 * @throws CommerceSaveCartException
	 * @throws InterruptedException
	 */
	@Test
	public void testGetSavedCardsSortingWithBaseSiteNoOrderStatus() throws CommerceSaveCartException, InterruptedException
	{
		final UserModel dejol = userService.getUserForUID("dejol");
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		userService.setCurrentUser(dejol);

		createSavedCarts(baseSite, dejol);

		sortAndTest(baseSite, dejol, null);
	}

	/**
	 * Test saves carts and checks that sorting criteria is handled correctly.
	 *
	 * @throws CommerceSaveCartException
	 * @throws InterruptedException
	 */
	@Test
	public void testGetSavedCardsSortingWithOrderStatusNoBaseSite() throws CommerceSaveCartException, InterruptedException
	{
		final UserModel dejol = userService.getUserForUID("dejol");
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		userService.setCurrentUser(dejol);

		final List<OrderStatus> orderStatus = new ArrayList<OrderStatus>();
		orderStatus.add(OrderStatus.CREATED);

		createSavedCarts(baseSite, dejol);

		sortAndTest(null, dejol, orderStatus);
	}

	/**
	 * Test saves carts and checks that sorting criteria is handled correctly.
	 *
	 * @throws CommerceSaveCartException
	 * @throws InterruptedException
	 */
	@Test
	public void testGetSavedCardsSortingWithOrderStatusAndBaseSite() throws CommerceSaveCartException, InterruptedException
	{
		final UserModel dejol = userService.getUserForUID("dejol");
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		userService.setCurrentUser(dejol);

		final List<OrderStatus> orderStatus = new ArrayList<OrderStatus>();
		orderStatus.add(OrderStatus.CREATED);

		createSavedCarts(baseSite, dejol);

		sortAndTest(baseSite, dejol, orderStatus);
	}

	protected void createSavedCart(final UserModel user, final BaseSiteModel baseSite, final String cartNamePrefix)
			throws CommerceSaveCartException
	{
		final CommerceSaveCartParameter commerceSaveCartParameter = new CommerceSaveCartParameter();
		commerceSaveCartParameter.setName(cartNamePrefix + "_Name");
		commerceSaveCartParameter.setDescription(cartNamePrefix + "_Description");

		final CartModel cartModel = cartFactory.createCart();
		cartModel.setSite(baseSite);
		cartModel.setUser(user);
		cartModel.setCode(cartNamePrefix + "_Code");
		cartModel.setTotalPrice(new Double(cartNamePrefix.length()));
		cartModel.setStatus(OrderStatus.CREATED);
		commerceSaveCartParameter.setCart(cartModel);

		commerceSaveCartService.saveCart(commerceSaveCartParameter);
	}

	protected void createSavedCarts(final BaseSiteModel baseSite, final UserModel user)
			throws CommerceSaveCartException, InterruptedException
	{
		createSavedCart(user, baseSite, CART_NAME_PREFIX_B);
		TimeUnit.MILLISECONDS.sleep(100);
		createSavedCart(user, baseSite, CART_NAME_PREFIX_A);
	}

	protected void sortAndTest(final BaseSiteModel baseSite, final UserModel user, final List<OrderStatus> orderStatus)
	{
		final PageableData pd = new PageableData();
		pd.setCurrentPage(0);
		pd.setPageSize(10);

		//sort by name
		pd.setSort(SORT_CODE_BY_NAME);
		SearchPageData<CartModel> savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		assertEquals(savedCarts.getResults().size(), 2);
		assertTrue(savedCarts.getResults().get(0).getName().startsWith(CART_NAME_PREFIX_A));

		//sort by saved date
		pd.setSort(SORT_CODE_BY_DATE_SAVED);
		savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		assertEquals(savedCarts.getResults().size(), 2);
		assertTrue(savedCarts.getResults().get(0).getName().startsWith(CART_NAME_PREFIX_A));

		//sort by modified date
		pd.setSort(SORT_CODE_BY_DATE_MODIFIED);
		savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		assertEquals(savedCarts.getResults().size(), 2);
		assertTrue(savedCarts.getResults().get(0).getName().startsWith(CART_NAME_PREFIX_A));

		//sort by code
		pd.setSort(SORT_CODE_BY_CODE);
		savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		assertEquals(savedCarts.getResults().size(), 2);
		assertTrue(savedCarts.getResults().get(0).getName().startsWith(CART_NAME_PREFIX_A));

		//sort by total price
		pd.setSort(SORT_CODE_BY_TOTAL);
		savedCarts = commerceSaveCartService.getSavedCartsForSiteAndUser(pd, baseSite, user, orderStatus);
		assertEquals(savedCarts.getResults().size(), 2);
		assertTrue(savedCarts.getResults().get(0).getName().startsWith(CART_NAME_PREFIX_A));
	}
}
