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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.SaveCartDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class DefaultSaveCartDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String USER = "abrode";
	private static final String CART_CODE = "abrodeCart";
	private static final String TEST_BASESITE1_UID = "testSite";
	private static final String TEST_BASESITE2_UID = "testSite2";

	@Resource
	private SaveCartDao saveCartDao;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private TimeService timeService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CartService cartService;

	@Before
	public void setUp() throws Exception
	{
		// importing test csv
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
	}

	@Test
	public void getSavedCartsCountForSiteAndUserTest()
	{
		final UserModel user = userService.getUserForUID(USER);
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(TEST_BASESITE1_UID);
		final BaseSiteModel baseSite2 = baseSiteService.getBaseSiteForUID(TEST_BASESITE2_UID);

		final int originalCountWithNullBaseSite = saveCartDao.getSavedCartsCountForSiteAndUser(null, user).intValue();
		final int originalCountWithBaseSite1 = saveCartDao.getSavedCartsCountForSiteAndUser(baseSite1, user).intValue();
		final int originalCountWithBaseSite2 = saveCartDao.getSavedCartsCountForSiteAndUser(baseSite2, user).intValue();

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode(CART_CODE);

		CartModel cartToBeSaved = flexibleSearchService.getModelByExample(modelByExample);
		populateCartModel(cartToBeSaved, user, null);

		//save 1 cart with save time is null
		cartToBeSaved.setSaveTime(null);
		modelService.save(cartToBeSaved);
		int countWithNullBaseSite = saveCartDao.getSavedCartsCountForSiteAndUser(null, user).intValue();
		assertEquals(originalCountWithNullBaseSite, countWithNullBaseSite);

		//save 1 cart with baseSite1
		cartToBeSaved.setSaveTime(timeService.getCurrentTime());
		cartToBeSaved.setSite(baseSite1);
		modelService.save(cartToBeSaved);

		countWithNullBaseSite = saveCartDao.getSavedCartsCountForSiteAndUser(null, user).intValue();
		final int countWithBaseSite1 = saveCartDao.getSavedCartsCountForSiteAndUser(baseSite1, user).intValue();
		assertEquals(originalCountWithBaseSite1 + 1, countWithBaseSite1);
		assertEquals(originalCountWithNullBaseSite + 1, countWithNullBaseSite);

		//save 1 session cart with baseSite2
		userService.setCurrentUser(user);
		cartToBeSaved = cartService.getSessionCart();
		populateCartModel(cartToBeSaved, user, baseSite2);
		modelService.save(cartToBeSaved);

		countWithNullBaseSite = saveCartDao.getSavedCartsCountForSiteAndUser(null, user).intValue();
		final int countWithBaseSite2 = saveCartDao.getSavedCartsCountForSiteAndUser(baseSite2, user).intValue();
		assertEquals(originalCountWithBaseSite2 + 1, countWithBaseSite2);
		assertEquals(originalCountWithNullBaseSite + 2, countWithNullBaseSite);
	}

	private void populateCartModel(final CartModel cart, final UserModel user, final BaseSiteModel baseSite)
	{
		cart.setName("name");
		cart.setDescription("description");
		cart.setSavedBy(user);
		cart.setSite(baseSite);

		final Date currentDate = timeService.getCurrentTime();
		cart.setSaveTime(currentDate);
	}

}
