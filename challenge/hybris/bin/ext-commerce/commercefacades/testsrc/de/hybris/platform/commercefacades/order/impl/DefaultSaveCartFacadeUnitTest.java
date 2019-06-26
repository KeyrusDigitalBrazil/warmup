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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Before;
import org.junit.Test;


public class DefaultSaveCartFacadeUnitTest
{
	private UserModel userModel;
	private BaseSiteModel baseSiteModel;

	private DefaultSaveCartFacade saveCartFacade;
	private CommerceSaveCartService saveCartService;
	private BaseSiteService baseSiteService;
	private UserService userService;

	@Before
	public void setUp()
	{
		userModel = mock(UserModel.class);
		baseSiteModel = mock(BaseSiteModel.class);

		saveCartFacade = new DefaultSaveCartFacade();

		baseSiteService = mock(BaseSiteService.class);
		saveCartFacade.setBaseSiteService(baseSiteService);

		userService = mock(UserService.class);
		saveCartFacade.setUserService(userService);

		saveCartService = mock(CommerceSaveCartService.class);
		saveCartFacade.setCommerceSaveCartService(saveCartService);
	}

	@Test
	public void testGetSavedCartsCountForCurrentUser()
	{
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(saveCartService.getSavedCartsCountForSiteAndUser(baseSiteModel, userModel)).thenReturn(new Integer(1));

		final int count = saveCartFacade.getSavedCartsCountForCurrentUser().intValue();
		assertEquals(1, count);
	}
}
