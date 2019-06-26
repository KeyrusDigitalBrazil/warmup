/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
@UnitTest
public class DefaultCMSNavigationDaoTest
{
	private DefaultCMSNavigationDao cmsNavigationDao;

	@Before
	public void createSampleCatalogStructure()
	{
		cmsNavigationDao = new DefaultCMSNavigationDao();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindPageNavigationEntriesByUid_nullUid()
	{
		// WHEN
		cmsNavigationDao.findNavigationEntryByUid(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindPageNavigationEntriesByUid_nullCatalogVersion()
	{
		// WHEN
		cmsNavigationDao.findNavigationEntryByUid("UID", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindPageNavigationEntriesByContentPage_nullContentPageParam()
	{
		// WHEN
		cmsNavigationDao.findNavigationEntriesByPage(null);
	}
}
