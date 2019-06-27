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
package de.hybris.platform.personalizationcms.model;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.enums.ActionType;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DynamicAttributeTests extends ServicelayerTest
{
	private static String CONTAINER_ID = "containerId";
	private static String COMPONENT_ID = "componentId";
	private static String AFFECTED_OBJECT_ID = CONTAINER_ID + "_" + COMPONENT_ID;

	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void shouldCalculateAffectedObjectKey()
	{
		final CxCmsActionModel action = new CxCmsActionModel();
		action.setComponentId(COMPONENT_ID);
		action.setContainerId(CONTAINER_ID);
		action.setCode("code" + new Date().getTime());
		action.setTarget("target");
		action.setType(ActionType.PLAIN);
		action.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
		modelService.save(action);

		Assert.assertEquals(AFFECTED_OBJECT_ID, action.getAffectedObjectKey());
	}

}
