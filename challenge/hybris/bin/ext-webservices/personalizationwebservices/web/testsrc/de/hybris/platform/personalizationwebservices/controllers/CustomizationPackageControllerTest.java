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
package de.hybris.platform.personalizationwebservices.controllers;

import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.assertVariationsEquals;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.creteCustomizationData;
import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.removeAllCustomizations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationfacades.customization.CustomizationFacade;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationfacades.variation.VariationFacade;
import de.hybris.platform.personalizationservices.enums.CxGroupingOperator;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationwebservices.validator.CustomizationDataValidator;
import de.hybris.platform.personalizationwebservices.validator.CustomizationPackageValidator;
import de.hybris.platform.personalizationwebservices.validator.SegmentDataValidator;
import de.hybris.platform.personalizationwebservices.validator.SegmentTriggerDataValidator;
import de.hybris.platform.personalizationwebservices.validator.VariationDataValidator;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.validators.FieldNotEmptyOrTooLongValidator;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;
import org.springframework.web.util.UriComponentsBuilder;


@IntegrationTest
public class CustomizationPackageControllerTest extends ServicelayerBaseTest
{
	private static final String CUSTOMIZATION_ID = "customization";
	private static final String CUSTOMIZATION_NAME = "customization";
	private static final String NEW_CUSTOMIZATION_ID = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";
	private final static String SEGMENT_ID = "segment0";
	private final static String CATALOG_ID = "testCatalog";
	private final static String CATALOG_VERSION_STAGE_ID = "Staged";
	private final static String VARIATION_ID = "variation";
	private final static String VARIATION_NAME = "variation";
	private final static String NEW_VARIATION_ID = "newVariation";
	private final static String NEW_VARIATION_NAME = "newVariationName";
	private final static String TRIGGER_ID = "trigger";
	private final static String NEW_TRIGGER_ID = "newTrigger";
	private final static String NEW_DESCRIPTION = "newDescription";
	protected static final String VERSION = "v1";
	private static final String BASE_LOCATION = "http://test.local:9090/" + VERSION + "/catalogs/" + CATALOG_ID
			+ "/catalogVersions/" + CATALOG_VERSION_STAGE_ID + "/";

	private CatalogModel catalog;
	private CatalogVersionModel catalogVersion;
	private CxCustomizationsGroupModel group;
	private CxSegmentModel segment;

	CustomizationPackageController controller;

	@Resource(name = "defaultCxCustomizationFacade")
	private CustomizationFacade customizationFacade;
	@Resource(name = "defaultCxVariationFacade")
	private VariationFacade variationFacade;
	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private UriComponentsBuilder uriComponentsBuilder;

	@Before
	public void setUp() throws Exception
	{
		final SegmentTriggerDataValidator triggerValidator = new SegmentTriggerDataValidator();
		triggerValidator.setSegmentValidator(new SegmentDataValidator());

		final FieldNotEmptyOrTooLongValidator fieldValidator = new FieldNotEmptyOrTooLongValidator();
		fieldValidator.setFieldPath("name");
		fieldValidator.setMaxLength(255);
		final Validator[] fieldValidators =
		{ fieldValidator };
		final CustomizationDataValidator customizationDataValidator = new CustomizationDataValidator();
		customizationDataValidator.setValidators(fieldValidators);
		final VariationDataValidator variationDataValidator = new VariationDataValidator();
		variationDataValidator.setValidators(fieldValidators);

		final CustomizationPackageValidator customizationValidator = new CustomizationPackageValidator();
		customizationValidator.setCustomizationValidator(customizationDataValidator);
		customizationValidator.setVariationValidator(variationDataValidator);
		customizationValidator.setTriggerValidator(triggerValidator);

		controller = new CustomizationPackageController(customizationFacade, customizationValidator);
		uriComponentsBuilder = new UriComponentBuilderStub();

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
	public void createCustomizationTest()
	{
		//given
		final CustomizationData customizatinData = new CustomizationData();
		customizatinData.setName(NEW_CUSTOMIZATION_NAME);
		customizatinData.setRank(Integer.valueOf(0));

		//when
		final ResponseEntity<CustomizationData> response = controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID,
				customizatinData, uriComponentsBuilder);

		//then
		final CustomizationData createdCustomization = response.getBody();
		assertNotNull(createdCustomization);
		assertNotNull(customizatinData.getCode());
		assertEquals(customizatinData.getName(), createdCustomization.getName());

		assertLocation("customizations/" + customizatinData.getCode(), response);

	}

	@Test
	public void createCustomizationPackageTest()
	{
		//given
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, NEW_TRIGGER_ID, SEGMENT_ID);

		//when
		final ResponseEntity<CustomizationData> response = controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID,
				customizationData, uriComponentsBuilder);
		final List<VariationData> variations = variationFacade.getVariations(NEW_CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		assertLocation("customizations/" + NEW_CUSTOMIZATION_ID, response);
		Assert.assertNotNull("Created customization should not be null", response.getBody());
		Assert.assertEquals("Invalid customization code", NEW_CUSTOMIZATION_ID, response.getBody().getCode());
		Assert.assertNotNull("Variations should not be null", variations);
		Assert.assertEquals("Variations size should be 1", 1, variations.size());
		assertVariationsEquals(customizationData.getVariations().get(0), variations.get(0));
	}

	@Test(expected = WebserviceValidationException.class)
	public void createInvalidCustomizationTest()
	{
		final CustomizationData customizationData = new CustomizationData();

		controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID, customizationData, uriComponentsBuilder);
	}

	@Test(expected = WebserviceValidationException.class)
	public void createCustomizationWithInvalidVariationTest()
	{
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, null);
		customizationData.getVariations().get(0).setName(null);

		controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID, customizationData, uriComponentsBuilder);
	}

	@Test(expected = WebserviceValidationException.class)
	public void createCustomizationWithInvalidTriggerTest()
	{
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, NEW_TRIGGER_ID, null);
		customizationData.getVariations().get(0).getTriggers().get(0).setCode(null);

		controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID, customizationData, uriComponentsBuilder);
	}

	@Test(expected = WebserviceValidationException.class)
	public void createCustomizationWithInvalidSegmentTest()
	{
		final CustomizationData customizationData = creteCustomizationData(NEW_CUSTOMIZATION_ID, NEW_CUSTOMIZATION_NAME,
				NEW_VARIATION_ID, NEW_VARIATION_NAME, NEW_TRIGGER_ID, SEGMENT_ID);
		final SegmentTriggerData segmentTrigger = (SegmentTriggerData) customizationData.getVariations().get(0).getTriggers()
				.get(0);
		segmentTrigger.getSegments().get(0).setCode(null);

		controller.createCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID, customizationData, uriComponentsBuilder);
	}

	@Test
	public void updateCustomizationPackageTest()
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
		controller.updateCustomization(CATALOG_ID, CATALOG_VERSION_STAGE_ID, CUSTOMIZATION_ID, customizationData);

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

	protected void assertLocation(final String expected, final ResponseEntity<CustomizationData> actual)
	{
		final URI location = actual.getHeaders().getLocation();
		assertNotNull(location);
		assertEquals(BASE_LOCATION + expected, location.toString());
	}
}
