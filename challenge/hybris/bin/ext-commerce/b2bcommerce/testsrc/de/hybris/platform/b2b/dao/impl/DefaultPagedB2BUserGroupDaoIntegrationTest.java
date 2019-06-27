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
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
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
public class DefaultPagedB2BUserGroupDaoIntegrationTest extends BaseCommerceBaseTest
{

	private static final String DUMMY = "dummy";
	private static final String DUMMY2 = "dummy2";

	@Resource
	private DefaultPagedB2BUserGroupDao pagedB2BUserGroupDao;

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
	public void testFindPagedUserGroupsExisting()
	{
		final B2BUserGroupModel userGroupModel = b2BDaoTestUtils.createUserGroup(DUMMY, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BUserGroupModel> b2BUserGroups = pagedB2BUserGroupDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(1, b2BUserGroups);
		assertEquals(userGroupModel.getName(), b2BUserGroups.getResults().get(0).getName());
	}

	@Test
	public void testFindPagedUserGroupsIsOrderedByCode()
	{
		final B2BUserGroupModel userGroupModel = b2BDaoTestUtils.createUserGroup(DUMMY, unit);
		final B2BUserGroupModel userGroupModel2 = b2BDaoTestUtils.createUserGroup(DUMMY2, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BUserGroupModel> b2BUserGroups = pagedB2BUserGroupDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, b2BUserGroups);
		assertEquals(userGroupModel.getName(), b2BUserGroups.getResults().get(0).getName());
		assertEquals(userGroupModel2.getName(), b2BUserGroups.getResults().get(1).getName());
	}

	@Test
	public void testFindPagedUserGroupsInexistent()
	{
		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BUserGroupModel> b2BUserGroups = pagedB2BUserGroupDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(0, b2BUserGroups);
	}

	@Test
	public void testPagination()
	{
		final int pageSize = 3;
		final B2BUserGroupModel[] models = new B2BUserGroupModel[5];
		for (int i = 0; i < models.length; i++)
		{
			models[i] = b2BDaoTestUtils.createUserGroup("ug" + i, unit);
		}
		final PageableData pageableData = createPageableData();
		pageableData.setPageSize(pageSize);

		// get first page of results
		pageableData.setCurrentPage(0);
		final SearchPageData<B2BUserGroupModel> results0 = pagedB2BUserGroupDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(pageSize, results0);
		// check if 1st 3 user groups are in the 1st page
		for (int i = 0; i < pageSize; i++)
		{
			assertEquals(models[i].getName(), results0.getResults().get(i).getName());
		}

		// get second page of results
		pageableData.setCurrentPage(1);
		final SearchPageData<B2BUserGroupModel> results1 = pagedB2BUserGroupDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, results1);
		// check if last 2 user groups are in the 2nd page
		for (int i = 0; i < 2; i++)
		{
			assertEquals(models[pageSize + i].getName(), results1.getResults().get(i).getName());
		}
	}
}
