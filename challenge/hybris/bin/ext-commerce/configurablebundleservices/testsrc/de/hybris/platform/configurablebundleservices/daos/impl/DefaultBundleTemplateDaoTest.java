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

package de.hybris.platform.configurablebundleservices.daos.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * JUnit test suite for {@link DefaultBundleTemplateDao}
 */
@UnitTest
public class DefaultBundleTemplateDaoTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DefaultBundleTemplateDao bundleTemplateDao;

	@Before
	public void setUp() throws Exception
	{
		bundleTemplateDao = new DefaultBundleTemplateDao();
	}

	@Test
	public void testFindBundleTemplatesByProductWhenNoProduct()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("productModel can not be null");

		bundleTemplateDao.findBundleTemplatesByProduct(null);
	}

	@Test
	public void testFindTemplatesByMasterOrderAndBundleNoWhenNoOrder()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("masterAbstractOrder can not be null");

		bundleTemplateDao.findTemplatesByMasterOrderAndBundleNo(null, 1);
	}
}
