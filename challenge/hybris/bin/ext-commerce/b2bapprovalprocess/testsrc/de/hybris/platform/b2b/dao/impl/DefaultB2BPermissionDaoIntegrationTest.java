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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BPermissionDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultB2BPermissionDao defaultB2BPermissionDao;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void testFindPermissionByCodeBudgetExceeded()
	{
		final B2BPermissionModel permission = defaultB2BPermissionDao.findPermissionByCode("2POE BUDGET");
		Assert.assertTrue("Permission was not found", permission instanceof B2BBudgetExceededPermissionModel);
	}

	@Test
	public void testFindPermissionByCodeOrderThreshold()
	{
		final B2BPermissionModel permission = defaultB2BPermissionDao.findPermissionByCode("2POE 1,000 EUR");
		Assert.assertTrue("Permission was not found", permission instanceof B2BOrderThresholdPermissionModel);
	}

	@Test
	public void testFindPermissionByCodeEmpty()
	{
		final B2BPermissionModel permission = defaultB2BPermissionDao.findPermissionByCode(StringUtils.EMPTY);
		Assert.assertNull("A permission was for an empty uid", permission);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindPermissionByCodeNull()
	{
		final B2BPermissionModel permission = defaultB2BPermissionDao.findPermissionByCode(null);
		Assert.assertNull("A permission was found for uid null", permission);
	}

}
