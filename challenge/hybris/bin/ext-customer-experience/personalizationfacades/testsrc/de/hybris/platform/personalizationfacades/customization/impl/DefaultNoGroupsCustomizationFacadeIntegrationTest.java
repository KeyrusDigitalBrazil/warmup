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
package de.hybris.platform.personalizationfacades.customization.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.customization.CustomizationFacade;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultNoGroupsCustomizationFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	private static final String NEW_CUSTOMIZATION_ID = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";

	@Resource(name = "defaultCxCustomizationFacade")
	private CustomizationFacade customizationFacade;

	@Before
	@Override
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationfacades/test/testdata_withoutcx_personalizationfacades.impex", "UTF-8"));
	}

	@Test
	public void createCustomizationTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(NEW_CUSTOMIZATION_ID);
		customizationData.setName(NEW_CUSTOMIZATION_NAME);

		//when
		final CustomizationData result = customizationFacade.createCustomization(customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_CUSTOMIZATION_ID, result.getCode());
		Assert.assertEquals(NEW_CUSTOMIZATION_NAME, result.getName());
	}

	@Test
	public void testCreateCustomizationWithRank()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(NEW_CUSTOMIZATION_ID);
		customizationData.setName(NEW_CUSTOMIZATION_NAME);
		final Integer rank = Integer.valueOf(0);
		customizationData.setRank(rank);

		//when
		final CustomizationData result = customizationFacade.createCustomization(customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_CUSTOMIZATION_ID, result.getCode());
		Assert.assertEquals(NEW_CUSTOMIZATION_NAME, result.getName());
		Assert.assertEquals(rank, result.getRank());
	}

}
