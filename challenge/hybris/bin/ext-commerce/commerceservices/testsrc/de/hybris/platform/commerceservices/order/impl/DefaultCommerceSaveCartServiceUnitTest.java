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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.SaveCartDao;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;


public class DefaultCommerceSaveCartServiceUnitTest
{
	private DefaultCommerceSaveCartService saveCartSerivce;

	private UserModel userModel;
	private BaseSiteModel baseSiteModel;

	private UserService userService;
	private SaveCartDao saveCartDao;

	@Before
	public void setUp()
	{
		saveCartSerivce = new DefaultCommerceSaveCartService();
		userService = mock(UserService.class);
		saveCartDao = mock(SaveCartDao.class);

		saveCartSerivce.setSaveCartDao(saveCartDao);
		saveCartSerivce.setUserService(userService);

		userModel = mock(UserModel.class);
		baseSiteModel = mock(BaseSiteModel.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetSavedCartsCountForNullUser()
	{
		saveCartSerivce.getSavedCartsCountForSiteAndUser(null, null);
	}

	@Test
	public void testGetSavedCartsCountForAnonymousUser()
	{
		when(Boolean.valueOf(userService.isAnonymousUser(userModel))).thenReturn(Boolean.TRUE);
		final int count = saveCartSerivce.getSavedCartsCountForSiteAndUser(null, userModel).intValue();
		assertEquals(0, count);
		verify(saveCartDao, never()).getSavedCartsCountForSiteAndUser(null, userModel);
	}

	@Test
	public void testGetSavedCartsCountForSiteAndUser()
	{
		when(Boolean.valueOf(userService.isAnonymousUser(userModel))).thenReturn(Boolean.FALSE);
		when(saveCartDao.getSavedCartsCountForSiteAndUser(null, userModel)).thenReturn(Integer.valueOf(2));
		int count = saveCartSerivce.getSavedCartsCountForSiteAndUser(null, userModel).intValue();
		assertEquals(2, count);

		when(saveCartDao.getSavedCartsCountForSiteAndUser(baseSiteModel, userModel)).thenReturn(Integer.valueOf(1));
		count = saveCartSerivce.getSavedCartsCountForSiteAndUser(baseSiteModel, userModel).intValue();
		assertEquals(1, count);
	}
}
