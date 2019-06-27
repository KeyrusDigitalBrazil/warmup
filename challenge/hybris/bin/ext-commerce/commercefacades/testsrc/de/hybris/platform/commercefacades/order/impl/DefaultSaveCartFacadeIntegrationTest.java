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
package de.hybris.platform.commercefacades.order.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class DefaultSaveCartFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String USER = "abrode";

	@Resource
	private SaveCartFacade saveCartFacade;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private CommerceSaveCartService commerceSaveCartService;

	@Resource
	private CartService cartService;

	@Resource
	private UserService userService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commercefacades/test/testCartFacade.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(TEST_BASESITE_UID, false);
		final UserModel user = userService.getUserForUID(USER);
		userService.setCurrentUser(user);
	}

	@Test
	public void testGetSavedCartsCountForCurrentUser() throws CommerceSaveCartException
	{
		final int originalCount = saveCartFacade.getSavedCartsCountForCurrentUser().intValue();

		final CommerceSaveCartParameter saveCartParameter = new CommerceSaveCartParameter();
		saveCartParameter.setName("name");
		saveCartParameter.setDescription("description");
		saveCartParameter.setCart(cartService.getSessionCart());
		commerceSaveCartService.saveCart(saveCartParameter);

		final int count = saveCartFacade.getSavedCartsCountForCurrentUser().intValue();
		assertEquals(originalCount + 1, count);

	}
}
