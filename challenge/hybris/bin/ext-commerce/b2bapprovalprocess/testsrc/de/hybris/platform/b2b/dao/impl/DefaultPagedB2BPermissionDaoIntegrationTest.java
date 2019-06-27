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
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
public class DefaultPagedB2BPermissionDaoIntegrationTest extends BaseCommerceBaseTest
{

	private static final String DUMMY = "dummy";
	private static final String DUMMY2 = "dummy2";

	@Resource
	private ModelService modelService;

	@Resource
	private DefaultPagedB2BPermissionDao pagedB2BPermissionDao;

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
	public void testFindPagedPermissionsExisting()
	{
		final B2BPermissionModel permissionModel = createPermission(true, DUMMY, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BPermissionModel> b2BPermissions = pagedB2BPermissionDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(1, b2BPermissions);
		assertEquals(permissionModel.getCode(), b2BPermissions.getResults().get(0).getCode());
	}

	@Test
	public void testFindPagedPermissionsIsOrderedByCode()
	{
		final B2BPermissionModel permissionModel = createPermission(true, DUMMY, unit);
		final B2BPermissionModel permissionModel2 = createPermission(true, DUMMY2, unit);

		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BPermissionModel> b2BPermissions = pagedB2BPermissionDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, b2BPermissions);
		assertEquals(permissionModel.getCode(), b2BPermissions.getResults().get(0).getCode());
		assertEquals(permissionModel2.getCode(), b2BPermissions.getResults().get(1).getCode());
	}

	@Test
	public void testFindPagedPermissionsInexistent()
	{
		final PageableData pageableData = createPageableData();
		final SearchPageData<B2BPermissionModel> b2BPermissions = pagedB2BPermissionDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(0, b2BPermissions);
	}

	@Test
	public void testPagination()
	{
		final int pageSize = 3;
		final B2BPermissionModel[] models = new B2BPermissionModel[5];
		for (int i = 0; i < models.length; i++)
		{
			models[i] = createPermission(true, "p" + i, unit);
		}
		final PageableData pageableData = createPageableData();
		pageableData.setPageSize(pageSize);

		// get first page of results
		pageableData.setCurrentPage(0);
		final SearchPageData<B2BPermissionModel> results0 = pagedB2BPermissionDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(pageSize, results0);
		// check if 1st 3 permissions are in the 1st page
		for (int i = 0; i < pageSize; i++)
		{
			assertEquals(models[i].getCode(), results0.getResults().get(i).getCode());
		}

		// get second page of results
		pageableData.setCurrentPage(1);
		final SearchPageData<B2BPermissionModel> results1 = pagedB2BPermissionDao.find(pageableData);
		b2BDaoTestUtils.assertResultsSize(2, results1);
		// check if last 2 permissions are in the 2nd page
		for (int i = 0; i < 2; i++)
		{
			assertEquals(models[pageSize + i].getCode(), results1.getResults().get(i).getCode());
		}
	}

	protected B2BPermissionModel createPermission(final boolean active, final String code, final B2BUnitModel unit)
	{
		final B2BPermissionModel permissionModel = modelService.create(B2BBudgetExceededPermissionModel.class);
		permissionModel.setActive(Boolean.valueOf(active));
		permissionModel.setCode(code);
		permissionModel.setUnit(unit);
		modelService.save(permissionModel);
		return permissionModel;
	}
}
