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

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
public class DefaultPagedB2BCostCenterDaoIntegrationTest extends BaseCommerceBaseTest
{

	private static final String DUMMY = "dummy";
	private static final String DUMMY2 = "dummy2";

	@Resource
	private DefaultPagedB2BCostCenterDao pagedB2BCostCenterDao;

	@Resource
	private B2BDaoTestUtils b2BDaoTestUtils;

	private B2BUnitModel unit;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		ServicelayerTest.createDefaultCatalog();
		CatalogManager.getInstance().createEssentialData(java.util.Collections.EMPTY_MAP, null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");

		unit = b2BDaoTestUtils.createUnit(DUMMY, DUMMY);
	}

	@Test
	public void testFindPagedCostCenters()
	{
		final B2BCostCenterModel costCenterModel = b2BDaoTestUtils.createCostCenter(true, DUMMY, DUMMY, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BCostCenterModel> costCenters = pagedB2BCostCenterDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(1, costCenters);
		assertEquals(costCenterModel.getCode(), costCenters.getResults().get(0).getCode());
	}

	@Test
	public void testFindPagedCostCentersOrderedByName()
	{
		final B2BCostCenterModel costCenterModel = b2BDaoTestUtils.createCostCenter(true, DUMMY, DUMMY, unit);
		final B2BCostCenterModel costCenterModel2 = b2BDaoTestUtils.createCostCenter(true, DUMMY2, DUMMY2, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BCostCenterModel> costCenters = pagedB2BCostCenterDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, costCenters);
		assertEquals(costCenterModel.getName(), costCenters.getResults().get(0).getName());
		assertEquals(costCenterModel2.getName(), costCenters.getResults().get(1).getName());
	}

	@Test
	public void testFindPagedCostCentersInexistent()
	{
		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BCostCenterModel> costCenters = pagedB2BCostCenterDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(0, costCenters);
	}

	@Test
	public void testPagination()
	{
		final int pageSize = 3;
		final B2BCostCenterModel[] models = new B2BCostCenterModel[5];
		for (int i = 0; i < models.length; i++)
		{
			models[i] = b2BDaoTestUtils.createCostCenter(true, "cc" + i, "cc" + i, unit);
		}
		final PageableData pageableData = createPageableData();
		pageableData.setPageSize(pageSize);

		// get first page of results
		pageableData.setCurrentPage(0);
		final SearchPageData<B2BCostCenterModel> results0 = pagedB2BCostCenterDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(pageSize, results0);
		// check if 1st 3 cost centers are in the 1st page
		for (int i = 0; i < pageSize; i++)
		{
			assertEquals(models[i].getName(), results0.getResults().get(i).getName());
		}

		// get second page of results
		pageableData.setCurrentPage(1);
		final SearchPageData<B2BCostCenterModel> results1 = pagedB2BCostCenterDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, results1);
		// check if last 2 cost centers are in the 2nd page
		for (int i = 0; i < 2; i++)
		{
			assertEquals(models[pageSize + i].getName(), results1.getResults().get(i).getName());
		}
	}
}
