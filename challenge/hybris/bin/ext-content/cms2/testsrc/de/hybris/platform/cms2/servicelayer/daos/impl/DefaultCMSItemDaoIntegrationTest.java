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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSItemDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultCMSItemDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String TEST_COMPONENT_NAME = "test component";
	private static final String TEST_COMPONENT2_NAME = "My component";
	private final String TEST_COMPONENT_UID = "testComponent1";

	@Resource
	private ModelService modelService;
	@Resource
	private CMSItemDao cmsItemDao;
	@Resource
	private CatalogVersionService catalogVersionService;

	private ABTestCMSComponentContainerModel component1;
	private ABTestCMSComponentContainerModel component2;
	private CatalogVersionModel catalogVersion1;
	private CatalogVersionModel catalogVersion2;

	@Before
	public void setUp() throws Exception
	{

		importCsv("/test/cmsCatalogVersionTestData.csv", "windows-1252");
		catalogVersion1 = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion1");
		catalogVersion2 = catalogVersionService.getCatalogVersion("cms_Catalog", "CatalogVersion2");

		component1 = new ABTestCMSComponentContainerModel();
		component2 = new ABTestCMSComponentContainerModel();

		component1.setCatalogVersion(catalogVersion1);
		component1.setUid(TEST_COMPONENT_UID);
		component1.setName(TEST_COMPONENT_NAME);
		modelService.save(component1);
		component2.setCatalogVersion(catalogVersion1);
		component2.setUid("testComponent2");
		component2.setName(TEST_COMPONENT2_NAME);
		modelService.save(component2);
	}

	@Test
	public void findByUidWillFindOneByRightUidAndCatalogVersion()
	{
		final CMSItemModel fetchedComponent = cmsItemDao.findByUid(TEST_COMPONENT_UID, catalogVersion1);

		final ABTestCMSComponentContainerModel persistent = modelService.get(component1.getPk());

		Assert.assertThat(fetchedComponent, is(persistent));
	}

	@Test
	public void findByUidWillFindNoneByRightUidAndWrongCatalogVersion()
	{
		final CMSItemModel fetchedComponent = cmsItemDao.findByUid(TEST_COMPONENT_UID, catalogVersion2);

		Assert.assertThat(fetchedComponent, nullValue());
	}


	@Test
	public void findByTypeAndNameWillFindOneByRightNameAndCatalogVersion()
	{
		final SearchResult<CMSItemModel> byTypeCodeAndName = cmsItemDao.findByTypeCodeAndName(catalogVersion1,
				ABTestCMSComponentContainerModel._TYPECODE, TEST_COMPONENT_NAME);

		Assert.assertThat(byTypeCodeAndName.getCount(), is(1));
	}

	@Test
	public void findByTypeAndNameWillFindOneByCaseInsensitiveNameAndCatalogVersion()
	{
		final SearchResult<CMSItemModel> byTypeCodeAndName = cmsItemDao.findByTypeCodeAndName(catalogVersion1,
				ABTestCMSComponentContainerModel._TYPECODE, TEST_COMPONENT_NAME.toUpperCase());

		Assert.assertThat(byTypeCodeAndName.getCount(), is(1));
	}


	@Test
	public void findByTypeAndNameWillNoneByWrongName()
	{
		final SearchResult<CMSItemModel> byTypeCodeAndName = cmsItemDao.findByTypeCodeAndName(catalogVersion1,
				ABTestCMSComponentContainerModel._TYPECODE, "other component");

		Assert.assertThat(byTypeCodeAndName.getCount(), is(0));
	}


	@Test
	public void findByTypeAndNameWillNoneByWrongCatalog()
	{
		final SearchResult<CMSItemModel> byTypeCodeAndName = cmsItemDao.findByTypeCodeAndName(catalogVersion2,
				ABTestCMSComponentContainerModel._TYPECODE, TEST_COMPONENT_NAME);

		Assert.assertThat(byTypeCodeAndName.getCount(), is(0));
	}

	@Test
	public void findByTypeAndNameWillNoneByWrongType()
	{
		final SearchResult<CMSItemModel> byTypeCodeAndName = cmsItemDao.findByTypeCodeAndName(catalogVersion1,
				CMSLinkComponentModel._TYPECODE, TEST_COMPONENT_NAME);

		Assert.assertThat(byTypeCodeAndName.getCount(), is(0));
	}

}
