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

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.customization.CustomizationFacade;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultCustomizationFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	private static final String NOTEXISTING_CUSTOMIZATION_ID = "nonExistingCustomization";
	private static final String NEW_CUSTOMIZATION_ID = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";
	private static final String LONG_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse nec est sed massa maximus ultricies vitae semper lacus. Mauris lobortis vel massa eu scelerisque. Sed ex tellus, cursus a mauris dignissim, rutrum vehicula magna. Aliquam nec condimentum augue. Aenean auctor eleifend nunc, ac amet.";

	@Resource(name = "defaultCxCustomizationFacade")
	private CustomizationFacade customizationFacade;

	@Test
	public void getCustomizationTest()
	{
		//when
		final CustomizationData result = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
		Assert.assertNull(result.getEnabledStartDate());
		Assert.assertNull(result.getEnabledEndDate());
		Assert.assertEquals(Boolean.TRUE, result.getActive());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingCustomizationTest()
	{
		//when
		customizationFacade.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomizationForWrongCatalogVersionTest()
	{
		//when
		customizationFacade.getCustomization(CUSTOMIZATION_ID_1, CATALOG_ID, CATALOG_VERSION_ONLINE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomizationForNotExistingCatalogTest()
	{
		//when
		customizationFacade.getCustomization(CUSTOMIZATION_ID, NOTEXISTING_CATALOG_ID, CATALOG_VERSION_ONLINE_ID);
	}

	@Test
	public void getCustomizationsTest()
	{
		//when
		final List<CustomizationData> resultList = customizationFacade.getCustomizations(CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(2, resultList.size());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomizationsForNotExistingCatalogTest()
	{
		//when
		customizationFacade.getCustomizations(NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
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

	@Test(expected = AlreadyExistsException.class)
	public void createAltreadyExistedCustomizationTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(CUSTOMIZATION_ID);
		customizationData.setName(CUSTOMIZATION_NAME);

		//when
		customizationFacade.createCustomization(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void createCustomizationWithExistingCodeForDifferentCatalogTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(CUSTOMIZATION_ID_1);
		customizationData.setName(CUSTOMIZATION_NAME_1);

		//when
		final CustomizationData result = customizationFacade.createCustomization(customizationData, CATALOG_ID,
				CATALOG_VERSION_ONLINE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID_1, result.getCode());
		Assert.assertEquals(CUSTOMIZATION_NAME_1, result.getName());
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

	@Test
	public void updateCustomizationTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(CUSTOMIZATION_ID);
		final Integer rank = Integer.valueOf(1);
		customizationData.setRank(rank);
		customizationData.setEnabledStartDate(new Date());
		customizationData.setEnabledEndDate(new Date());
		customizationData.setName(NEW_CUSTOMIZATION_NAME);

		//when
		final CustomizationData result = customizationFacade.updateCustomization(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
		Assert.assertEquals(rank, result.getRank());
		Assert.assertEquals(customizationData.getEnabledStartDate(), result.getEnabledStartDate());
		Assert.assertEquals(customizationData.getEnabledEndDate(), result.getEnabledEndDate());
		Assert.assertEquals(customizationData.getName(), result.getName());
		Assert.assertEquals(Boolean.FALSE, result.getActive());
	}

	@Test
	public void updateCustomizationDescriptionTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(CUSTOMIZATION_ID);
		customizationData.setName(CUSTOMIZATION_NAME_1);
		customizationData.setDescription(LONG_DESCRIPTION);


		//when
		final CustomizationData result = customizationFacade.updateCustomization(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
		Assert.assertEquals(customizationData.getName(), result.getName());
		Assert.assertEquals(customizationData.getDescription(), result.getDescription());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingCustomizationTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode(NOTEXISTING_CUSTOMIZATION_ID);

		//when
		customizationFacade.updateCustomization(NOTEXISTING_CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test
	public void updateCustomizationWithCodeConflictTest()
	{
		//given
		final CustomizationData customizationData = new CustomizationData();
		customizationData.setCode("customization2");
		customizationData.setName("customization2");

		//when
		final CustomizationData result = customizationFacade.updateCustomization(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
	}

	@Test
	public void removeCustomizationTest()
	{
		//given
		boolean customizationRemoved = false;

		//when
		customizationFacade.removeCustomization(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		try
		{
			customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			customizationRemoved = true;
		}
		assertTrue(customizationRemoved);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void removeNotExistingCustomizationTest()
	{
		//when
		customizationFacade.removeCustomization(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}
}
