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

import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.assertVariationsEquals;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.createSegmentTriggerData;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.createVariationData;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.creteCustomizationData;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.removeAllCustomizations;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationfacades.customization.CustomizationFacade;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.DefaultTriggerData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.variation.VariationFacade;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCustomizationFacadeManagingTransactionMethodsTest extends ServicelayerBaseTest
{
	private static final String CUSTOMIZATION_ID = "customization";
	private static final String CUSTOMIZATION_NAME = "customization";
	private static final String NEW_CUSTOMIZATION_ID = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";
	private static final String NOT_EXISTING_SEGMENT_ID = "notExistingSegment";
	private static final String SEGMENT_ID = "segment0";
	private static final String CATALOG_ID = "testCatalog";
	private static final String CATALOG_VERSION_STAGE_ID = "Staged";
	private static final String VARIATION_ID = "variation";
	private static final String VARIATION_NAME = "variation";
	private static final String NEW_VARIATION_ID = "newVariation";
	private static final String NEW_VARIATION_NAME = "newVariationName";
	private static final String TRIGGER_ID = "trigger";
	private static final String NEW_TRIGGER_ID = "newTrigger";
	private static final String NEW_DESCRIPTION = "newDescription";

	@Resource(name = "defaultCxCustomizationFacade")
	private CustomizationFacade customizationFacade;

	@Resource(name = "defaultCxVariationFacade")
	private VariationFacade variationFacade;

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CatalogModel catalog;
	private CatalogVersionModel catalogVersion;
	private CxCustomizationsGroupModel group;
	private CxSegmentModel segment;

	@Before
	public void setUp() throws Exception
	{
		catalog = modelService.create(CatalogModel.class);
		catalog.setId(CATALOG_ID);
		catalog.setName(CATALOG_ID);
		modelService.save(catalog);

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setVersion(CATALOG_VERSION_STAGE_ID);
		catalogVersion.setCatalog(catalog);
		modelService.save(catalog);

		group = modelService.create(CxCustomizationsGroupModel.class);
		group.setCode("default");
		group.setCatalogVersion(catalogVersion);
		modelService.save(group);

		segment = modelService.create(CxSegmentModel.class);
		segment.setCode(SEGMENT_ID);
		modelService.save(segment);
	}

	@After
	public void cleanUp() throws Exception
	{
		removeAllCustomizations(flexibleSearchService, modelService);
		modelService.remove(segment);
		modelService.remove(group);
		modelService.remove(catalogVersion);
		modelService.remove(catalog);
	}

	@Test
	public void createCustomizationWithRelatedObjectsTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, NEW_TRIGGER_ID, SEGMENT_ID);

		//when
		final CustomizationData customization = customizationFacade.createCustomizationWithRelatedObjects(customizationData,
				CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		final List<VariationData> variations = variationFacade.getVariations(NEW_CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull("Created customization should not be null", customization);
		Assert.assertEquals("Invalid customization code", NEW_CUSTOMIZATION_ID, customization.getCode());
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		assertVariationsEquals(customizationData.getVariations().get(0), variations.get(0));
	}


	@Test
	public void createCustomizationWithDuplicatedVariatonTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, null, null);
		final VariationData variation = customizationData.getVariations().get(0);
		customizationData.setVariations(Arrays.asList(variation, variation));

		//when
		boolean customizationNotFound = false;
		boolean customizationNotCreated = false;
		try
		{
			customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final AlreadyExistsException e)
		{
			customizationNotCreated = true;
		}
		try
		{
			final CustomizationData cust = customizationFacade.getCustomization(NEW_CUSTOMIZATION_ID, CATALOG_ID,
					CATALOG_VERSION_STAGE_ID);
			if (cust != null)
			{
				customizationNotFound = false;
			}
		}
		catch (final UnknownIdentifierException e)
		{
			customizationNotFound = true;
		}

		//then
		Assert.assertTrue("Customization should not be created", customizationNotCreated);
		Assert.assertTrue("Customization should not be found", customizationNotFound);
	}

	@Test
	public void createCustomizationWhithIncorrectTriggerTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, NEW_TRIGGER_ID, NOT_EXISTING_SEGMENT_ID);

		//when
		boolean customizationNotFound = false;
		boolean customizationNotCreated = false;
		try
		{
			customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			customizationNotCreated = true;
		}
		try
		{
			customizationFacade.getCustomization(NEW_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			customizationNotFound = true;
		}

		//then
		Assert.assertTrue("Customization should not be created", customizationNotCreated);
		Assert.assertTrue("Customization should not be found", customizationNotFound);
	}

	@Test
	public void updateCustomizationWithRelatedObjectsTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		customizationData.setDescription(NEW_DESCRIPTION);
		VariationData variation = customizationData.getVariations().get(0);
		Assert.assertEquals(ItemStatus.ENABLED, variation.getStatus());
		variation.setStatus(ItemStatus.DISABLED);
		final String newGroupByValue = CxGroupingOperator.OR.getCode();
		((SegmentTriggerData) variation.getTriggers().get(0)).setGroupBy(newGroupByValue);

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final CustomizationData foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final List<VariationData> variations = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertEquals("Customization description should be updated", NEW_DESCRIPTION, foundCustomization.getDescription());
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		variation = variations.get(0);
		Assert.assertEquals("Variation should be updated", ItemStatus.DISABLED, variation.getStatus());
		Assert.assertEquals("Variation should not be active", Boolean.FALSE, variation.getActive());
		Assert.assertNotNull("Triggers should not be null", variation.getTriggers());
		Assert.assertEquals("Triggers size should be 1", 1, variation.getTriggers().size());
		Assert.assertEquals("Trigger groupBy attribute should be updated", newGroupByValue,
				((SegmentTriggerData) variation.getTriggers().get(0)).getGroupBy());
	}

	@Test
	public void updateCustomizationWithNewObjectsTest()
	{
		//given
		customizationFacade.createCustomization(
				creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, null, null, null, null), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, NEW_VARIATION_ID,
				NEW_VARIATION_NAME, NEW_TRIGGER_ID, SEGMENT_ID);

		//when
		final CustomizationData customization = customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID,
				customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		final List<VariationData> variations = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull("Created customization should not be null", customization);
		Assert.assertEquals("Invalid customization code", CUSTOMIZATION_ID, customization.getCode());
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		assertVariationsEquals(customizationData.getVariations().get(0), variations.get(0));
	}

	@Test
	public void updateCustomizationWithVariationRemovedTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		customizationData.setVariations(Collections.singletonList(createVariationData(NEW_VARIATION_ID, NEW_VARIATION_NAME, null)));

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final CustomizationData foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertEquals("Variations size should be 1", 1, foundCustomization.getVariations().size());
		Assert.assertEquals("New variation should be present ", NEW_VARIATION_ID,
				foundCustomization.getVariations().get(0).getCode());
	}

	@Test
	public void updateCustomizationWithAllVariationsRemovedTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		CustomizationData foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		customizationData.setVariations(null);

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertEquals("Variations should be empty", 0, foundCustomization.getVariations().size());
	}

	@Test
	public void updateCustomizationWithAllTriggersRemovedTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		List<VariationData> variations = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		customizationData.getVariations().get(0).setTriggers(null);

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		variations = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		Assert.assertEquals("Triggers should be empty", 0, variations.get(0).getTriggers().size());
	}

	@Test
	public void updateCustomizationWithRemovedTriggerTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		final SegmentTriggerData newSegmentTrigger = createSegmentTriggerData(NEW_TRIGGER_ID, SEGMENT_ID);
		customizationData.getVariations().get(0).setTriggers(Collections.singletonList(newSegmentTrigger));

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final List<VariationData> variations = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		Assert.assertEquals("Triggers should have size 1", 1, variations.get(0).getTriggers().size());
		Assert.assertEquals("There should be only new trigger", NEW_TRIGGER_ID, variations.get(0).getTriggers().get(0).getCode());
	}

	@Test
	public void updateCustomizationWithDefaultTriggerTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		customizationFacade.createCustomizationWithRelatedObjects(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		final DefaultTriggerData newDefaultTrigger = new DefaultTriggerData();
		customizationData.getVariations().get(0).setTriggers(Collections.singletonList(newDefaultTrigger));

		//when
		customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final List<VariationData> variations = variationFacade
				.getVariations(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		Assert.assertEquals("Triggers should have size 1", 1, variations.get(0).getTriggers().size());
		Assert.assertTrue("There should be only default trigger",
				variations.get(0).getTriggers().get(0) instanceof DefaultTriggerData);
	}

	@Test
	public void updateCustomizationWithDuplicatedVariatonTest()
	{
		//given
		customizationFacade.createCustomization(
				creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, null, null, null, null), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, null, null);
		customizationData.setDescription(NEW_DESCRIPTION);
		final VariationData variation = customizationData.getVariations().get(0);
		customizationData.setVariations(Arrays.asList(variation, variation));

		//when
		boolean customizationNotUpdated = false;
		try
		{
			customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
					CATALOG_VERSION_STAGE_ID);
		}
		catch (final AlreadyExistsException e)
		{
			customizationNotUpdated = true;
		}

		final CustomizationData foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);


		//then
		Assert.assertTrue("Customization should not be updated", customizationNotUpdated);
		Assert.assertNotEquals(NEW_DESCRIPTION, foundCustomization.getDescription());
	}

	@Test
	public void updateCustomizationWhithIncorrectTriggerTest()
	{
		//given
		customizationFacade.createCustomizationWithRelatedObjects(
				creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID, VARIATION_NAME, null, null), CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		final CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID,
				VARIATION_NAME, NEW_TRIGGER_ID, NOT_EXISTING_SEGMENT_ID);
		customizationData.setDescription(NEW_DESCRIPTION);
		customizationData.getVariations().get(0).setStatus(ItemStatus.DISABLED);

		//when
		boolean customizationNotUpdated = false;
		try
		{
			customizationFacade.updateCustomizationWithRelatedObjects(CUSTOMIZATION_ID, customizationData, CATALOG_ID,
					CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			customizationNotUpdated = true;
		}

		final CustomizationData foundCustomization = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertTrue("Customization should not be updated", customizationNotUpdated);
		Assert.assertNotEquals("Customization description should not be updated", NEW_DESCRIPTION,
				foundCustomization.getDescription());
		Assert.assertNotNull("Variations should not be null", foundCustomization.getVariations());
		Assert.assertEquals("Variations size should be 1", 1, foundCustomization.getVariations().size());
		Assert.assertEquals("Variation should not be updated", ItemStatus.ENABLED,
				foundCustomization.getVariations().get(0).getStatus());
	}
}
