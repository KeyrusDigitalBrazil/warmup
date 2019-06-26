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
package de.hybris.platform.b2b.company.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BCommerceB2BUserGroupServiceIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private DefaultB2BCommerceB2BUserGroupService defaultB2BCommerceB2BUserGroupService;

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
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void testRemoveUserGroup()
	{
		defaultB2BCommerceB2BUserGroupService.removeUserGroup("b2bTestGroup");
		Assert.assertNull("User group has not been removed",
				defaultB2BCommerceB2BUserGroupService.getUserGroupForUID("b2bTestGroup", B2BUserGroupModel.class));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testRemoveUserGroupUidEmpty()
	{
		defaultB2BCommerceB2BUserGroupService.removeUserGroup(StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserGroupUidNull()
	{
		defaultB2BCommerceB2BUserGroupService.removeUserGroup(null);
	}
}
