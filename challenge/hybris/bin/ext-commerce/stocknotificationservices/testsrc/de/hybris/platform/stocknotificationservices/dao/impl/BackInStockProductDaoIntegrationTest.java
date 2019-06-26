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
package de.hybris.platform.stocknotificationservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.stocknotificationservices.dao.BackInStockProductInterestDao;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class BackInStockProductDaoIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private BackInStockProductInterestDao backInStockProductInterestDao;

	@Before
	public void setup() throws IOException
	{
	}

	@Test
	public void testFindBackInStockProductInterests_Empty()
	{
		final List<ProductInterestModel> productIntersts = backInStockProductInterestDao.findBackInStorkProductInterests();
		final int actualSize = productIntersts.size();
		final int expectedSize = 0;

		Assert.assertEquals(expectedSize, actualSize);
	}

	@Test
	public void testFindBackInStockProductInterests() throws ImpExException
	{
		importCsv("/stocknotificationservices/test/impex/stocklevelstatusjob-test-data.impex", "utf-8");

		final List<ProductInterestModel> productIntersts = backInStockProductInterestDao.findBackInStorkProductInterests();
		final int actualSize = productIntersts.size();
		final int expectedSize = 7;

		Assert.assertEquals(expectedSize, actualSize);
	}
	@Test
	public void testFindBackInStockProductInterests_QueryResult_Null()
	{
		final List<ProductInterestModel> productIntersts = backInStockProductInterestDao.findBackInStorkProductInterests();
		Assert.assertTrue(CollectionUtils.isEmpty(productIntersts));
	}
}
