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
package de.hybris.platform.marketplaceservices.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultMarketplaceCMSComponentDaoTest extends ServicelayerTransactionalTest
{
	private static final String CONTENTSLOT_UID = "testSlot";
	private static final String COMPONENT_UID1 = "testComponent1";
	private static final String COMPONENT_UID2 = "testComponent2";
	private static final String COMPONENT_UID3 = "testComponent3";
	private static final String CATALOG_ID = "cms_Catalog";
	private static final String CATALOGVERSION1 = "CatalogVersion1";
	private static final String CATALOGVERSION2 = "CatalogVersion2";

	@Resource(name = "defaultMarketplaceCMSComponentDao")
	private DefaultMarketplaceCMSComponentDao defaultMarketplaceCMSComponentDao;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ModelService modelService;

	private ABTestCMSComponentContainerModel component1;
	private ABTestCMSComponentContainerModel component2;
	private ABTestCMSComponentContainerModel component3;
	private CatalogVersionModel catalogVersion1;
	private CatalogVersionModel catalogVersion2;

	@Before
	public void setUp() throws Exception
	{

		importCsv("/marketplaceservices/test/testCMSCatalogVersionData.csv", "windows-1252");
		catalogVersion1 = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOGVERSION1);
		catalogVersion2 = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOGVERSION2);

		final ContentSlotModel contentSlot1 = new ContentSlotModel();
		contentSlot1.setUid(CONTENTSLOT_UID);
		contentSlot1.setCatalogVersion(catalogVersion1);

		final ContentSlotModel contentSlot2 = new ContentSlotModel();
		contentSlot2.setUid(CONTENTSLOT_UID);
		contentSlot2.setCatalogVersion(catalogVersion2);

		component1 = new ABTestCMSComponentContainerModel();
		component2 = new ABTestCMSComponentContainerModel();
		component3 = new ABTestCMSComponentContainerModel();

		component1.setCatalogVersion(catalogVersion1);
		component1.setUid(COMPONENT_UID1);

		component2.setCatalogVersion(catalogVersion1);
		component2.setUid(COMPONENT_UID2);

		component3.setCatalogVersion(catalogVersion2);
		component3.setUid(COMPONENT_UID3);

		contentSlot1.setCmsComponents(Arrays.asList(component1, component2));
		contentSlot2.setCmsComponents(Arrays.asList(component3));
		modelService.save(component1);
		modelService.save(component2);
		modelService.save(component3);
		modelService.save(contentSlot1);
		modelService.save(contentSlot2);
	}

	@Test
	public void testFindCMSComponentsByContentSlot()
	{

		final List<AbstractCMSComponentModel> components = defaultMarketplaceCMSComponentDao
				.findCMSComponentsByContentSlot(CONTENTSLOT_UID, Arrays.asList(catalogVersion1, catalogVersion2));

		assertEquals(components.size(), 3);
		assertContainsComponentUid(components, COMPONENT_UID1);
		assertContainsComponentUid(components, COMPONENT_UID2);
		assertContainsComponentUid(components, COMPONENT_UID3);
	}

	protected void assertContainsComponentUid(final List<AbstractCMSComponentModel> components1, final String componentUid)
	{
		assertTrue(components1.stream().map(component -> component.getUid()).anyMatch(uid -> uid.equals(componentUid)));
	}
}
