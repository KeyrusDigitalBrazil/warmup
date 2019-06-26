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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.action.ActionFacade;
import de.hybris.platform.personalizationfacades.customization.CustomizationFacade;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.variation.VariationFacade;
import de.hybris.platform.personalizationwebservices.data.CustomizationListWsDTO;
import de.hybris.platform.personalizationwebservices.data.VariationListWsDTO;
import de.hybris.platform.personalizationwebservices.validator.ActionDataListValidator;
import de.hybris.platform.personalizationwebservices.validator.ActionDataValidator;
import de.hybris.platform.personalizationwebservices.validator.CustomizationDataValidator;
import de.hybris.platform.personalizationwebservices.validator.VariationDataValidator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.errors.exceptions.CodeConflictException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.validators.FieldNotEmptyOrTooLongValidator;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Validator;


@IntegrationTest
public class CustomizationControllerTest extends BaseControllerTest
{
	private static final String CUSTOMIZATION = "customization1";
	private static final String CUSTOMIZATION_NAME = "customization1";
	private static final String NONEXISTING_CUSTOMIZATION = "customization1000";
	private static final String NONEXISTING_CUSTOMIZATION_NAME = "customization1000";
	private static final String VARIATION = "variation1";
	private static final String VARIATION_NAME = "variation1";
	private static final String NONEXISTING_VARIATION = "variation1000";
	private static final String NONEXISTING_VARIATION_NAME = "variation1000";

	private static final Integer ONE = Integer.valueOf(1);
	private static final Integer TWO = Integer.valueOf(2);

	CustomizationController controller;
	VariationController vcontroller;
	ActionController acontroller;


	@Resource(name = "defaultCxCustomizationFacade")
	CustomizationFacade cxCustomizationFacade;
	@Resource(name = "defaultCxVariationFacade")
	VariationFacade cxVariationFacade;
	@Resource(name = "defaultCxActionFacade")
	ActionFacade cxActionFacade;
	@Resource
	ModelService modelService;
	@Resource
	WebPaginationUtils webPaginationUtils;

	@Before
	public void setUp() throws Exception
	{
		final ActionDataListValidator actionDataList = new ActionDataListValidator();
		actionDataList.setActionValidator(new ActionDataValidator());

		final FieldNotEmptyOrTooLongValidator fieldValidator = new FieldNotEmptyOrTooLongValidator();
		fieldValidator.setFieldPath("name");
		fieldValidator.setMaxLength(255);
		final Validator[] fieldValidators =
		{ fieldValidator };

		final CustomizationDataValidator customizationDataValidator = new CustomizationDataValidator();
		customizationDataValidator.setValidators(fieldValidators);
		controller = new CustomizationController(cxCustomizationFacade, customizationDataValidator);
		controller.setWebPaginationUtils(webPaginationUtils);

		final VariationDataValidator variationDataValidator = new VariationDataValidator();
		variationDataValidator.setValidators(fieldValidators);
		vcontroller = new VariationController(cxVariationFacade, variationDataValidator);
		vcontroller.setWebPaginationUtils(webPaginationUtils);

		acontroller = new ActionController(cxActionFacade, new ActionDataListValidator(), actionDataList);
		acontroller.setWebPaginationUtils(webPaginationUtils);
	}

	@Test
	public void getAllCustomizationsTest()
	{
		//when
		final CustomizationListWsDTO customizations = controller.getCustomizations(CATALOG, CATALOG_VERSION,
				Collections.emptyMap());

		//then
		assertNotNull(customizations);
		assertNotNull(customizations.getCustomizations());
		assertEquals(5, customizations.getCustomizations().size());
	}

	@Test
	public void getCustomizationByIdTest()
	{
		//when
		final CustomizationData customization = controller.getCustomization(CATALOG, CATALOG_VERSION, CUSTOMIZATION);

		//then
		assertNotNull("Customization shouldn't be empty", customization);
		assertEquals("Invalid customizaiton code", CUSTOMIZATION, customization.getCode());
		assertNotNull("Variations in customization shouldn't be empty", customization.getVariations());
		assertEquals("Invaid number of variations in customziation", 11, customization.getVariations().size());
	}

	@Test(expected = NotFoundException.class)
	public void getNonexistingCustomizationByIdTest()
	{
		controller.getCustomization(CATALOG, CATALOG_VERSION, NONEXISTING_CUSTOMIZATION);
	}

	@Test
	public void createCustomizationTest()
	{
		//given
		final Date date = new Date();

		final CustomizationData dto = new CustomizationData();
		dto.setName(NONEXISTING_CUSTOMIZATION_NAME);
		dto.setRank(ONE);
		dto.setEnabledEndDate(date);

		//when
		final ResponseEntity<CustomizationData> response = controller.createCustomization(CATALOG, CATALOG_VERSION, dto,
				getUriComponentsBuilder());

		//then

		final CustomizationData body = response.getBody();
		assertNotNull(body);
		assertNotNull(body.getCode());
		assertEquals(dto.getName(), body.getName());
		assertEquals(dto.getEnabledEndDate(), body.getEnabledEndDate());

		assertLocationWithCatalog("customizations/" + body.getCode(), response);

		final CustomizationListWsDTO customizations = controller.getCustomizations(CATALOG, CATALOG_VERSION,
				Collections.emptyMap());
		final Set<String> codeSet = customizations.getCustomizations().stream().map(c -> c.getCode()).collect(Collectors.toSet());
		assertTrue("Customization was not created properly.", codeSet.contains(body.getCode()));

		final Set<Integer> rankSet = customizations.getCustomizations().stream().map(c -> c.getRank()).collect(Collectors.toSet());
		assertEquals("Customization rank was not updated properly.", customizations.getCustomizations().size(), rankSet.size());
	}

	@Test(expected = WebserviceValidationException.class)
	public void createIncompleteCustomizationTest()
	{
		final CustomizationData dto = new CustomizationData();

		controller.createCustomization(CATALOG, CATALOG_VERSION, dto, getUriComponentsBuilder());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createExistingCustomizationTest()
	{
		//given
		final CustomizationData dto = new CustomizationData();
		dto.setCode(CUSTOMIZATION);
		dto.setName(CUSTOMIZATION_NAME);
		dto.setRank(ONE);

		//when
		controller.createCustomization(CATALOG, CATALOG_VERSION, dto, getUriComponentsBuilder());
	}

	@Test
	public void updateCustomizationTest()
	{
		//given
		final CustomizationData dto = new CustomizationData();
		dto.setCode(CUSTOMIZATION);
		dto.setName(CUSTOMIZATION_NAME);
		dto.setRank(TWO);

		//when
		final CustomizationData updateCustomization = controller.updateCustomization(CATALOG, CATALOG_VERSION, CUSTOMIZATION, dto);

		//then
		assertEquals(dto.getCode(), updateCustomization.getCode());
		assertEquals(dto.getRank(), updateCustomization.getRank());

		final CustomizationData customization = controller.getCustomization(CATALOG, CATALOG_VERSION, CUSTOMIZATION);
		assertEquals(dto.getRank(), customization.getRank());
	}

	@Test(expected = CodeConflictException.class)
	public void updateCustomizationWithInconsistenCodeTest()
	{
		//given
		final CustomizationData dto = new CustomizationData();
		dto.setCode(CUSTOMIZATION);
		dto.setName(CUSTOMIZATION_NAME);
		dto.setRank(TWO);

		//when
		controller.updateCustomization(CATALOG, CATALOG_VERSION, NONEXISTING_CUSTOMIZATION, dto);
	}

	@Test(expected = NotFoundException.class)
	public void updateNonexistingCustomizationTest()
	{
		//given
		final CustomizationData dto = new CustomizationData();
		dto.setCode(NONEXISTING_CUSTOMIZATION);
		dto.setName(NONEXISTING_CUSTOMIZATION_NAME);
		dto.setRank(TWO);

		//when
		controller.updateCustomization(CATALOG, CATALOG_VERSION, NONEXISTING_CUSTOMIZATION, dto);
	}

	@Test
	public void deleteCustomizationTest()
	{
		//when
		controller.deleteCustomization(CATALOG, CATALOG_VERSION, CUSTOMIZATION);

		//then
		try
		{
			controller.getCustomization(CATALOG, CATALOG_VERSION, CUSTOMIZATION);
			fail("Customization should be deleted");
		}
		catch (final NotFoundException e)
		{
			//OK
		}
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonexistingCustomizationTest()
	{
		//when
		controller.deleteCustomization(CATALOG, CATALOG_VERSION, NONEXISTING_CUSTOMIZATION);
	}

	@Test
	public void getAllVariationsForCustomizationTest()
	{
		//when
		final VariationListWsDTO variations = vcontroller.getVariations(CATALOG, CATALOG_VERSION, CUSTOMIZATION);

		//then
		assertNotNull(variations);
		assertNotNull(variations.getVariations());
		assertEquals(11, variations.getVariations().size());
	}

	@Test
	public void getVariationByIdFroCustomizationTest()
	{
		//when
		final VariationData variation = vcontroller.getVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION);

		//then
		assertNotNull(variation);
		assertEquals(VARIATION, variation.getCode());
	}

	@Test(expected = NotFoundException.class)
	public void getNonexistingVariationByIdTest()
	{
		//when
		vcontroller.getVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, NONEXISTING_VARIATION);
	}

	@Test(expected = NotFoundException.class)
	public void getVariationByIdFromInvalidCustomizationTest()
	{
		//when
		vcontroller.getVariation(CATALOG, CATALOG_VERSION, NONEXISTING_CUSTOMIZATION, VARIATION);
	}

	@Test
	public void createVariationInCustomizationTest()
	{
		//given
		final VariationData dto = new VariationData();
		dto.setName(NONEXISTING_VARIATION_NAME);
		dto.setRank(ONE);
		dto.setStatus(ItemStatus.ENABLED);

		//when
		final ResponseEntity<VariationData> response = vcontroller.createVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, dto,
				getUriComponentsBuilder());

		//then
		final VariationData body = response.getBody();
		assertNotNull(body);
		assertNotNull(body.getCode());
		assertEquals(dto.getCode(), body.getCode());
		assertLocationWithCatalog("customizations/" + CUSTOMIZATION + "/variations/" + body.getCode(), response);

		final VariationData variation = vcontroller.getVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, body.getCode());
		assertNotNull(variation);
		assertEquals(body.getCode(), variation.getCode());
		assertEquals(ItemStatus.ENABLED, variation.getStatus());
		assertEquals(dto.getRank(), variation.getRank());

		final VariationListWsDTO variations = vcontroller.getVariations(CATALOG, CATALOG_VERSION, CUSTOMIZATION);
		final Set<Integer> rankSet = variations.getVariations().stream().map(c -> c.getRank()).collect(Collectors.toSet());
		assertEquals("Variation rank was not updated properly.", variations.getVariations().size(), rankSet.size());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createExisitngVariationInCustomizationTest()
	{
		//given
		final VariationData dto = new VariationData();
		dto.setCode(VARIATION);
		dto.setName(VARIATION_NAME);
		dto.setRank(ONE);
		dto.setStatus(ItemStatus.ENABLED);

		//	when
		vcontroller.createVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, dto, getUriComponentsBuilder());
	}

	@Test
	public void updateVariationTest()
	{
		//given
		final VariationData dto = new VariationData();
		dto.setCode(VARIATION);
		dto.setName(VARIATION_NAME);
		dto.setRank(Integer.valueOf(5));
		dto.setStatus(ItemStatus.DISABLED);

		//when
		final VariationData updateVariation = vcontroller.updateVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION, dto);

		//then
		assertNotNull(updateVariation);
		assertEquals(dto.getRank(), updateVariation.getRank());
		assertEquals(dto.getStatus(), updateVariation.getStatus());

		final VariationData result = vcontroller.getVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION);
		assertNotNull(result);
		assertEquals(dto.getRank(), result.getRank());
		assertEquals(dto.getStatus(), result.getStatus());
	}

	@Test(expected = CodeConflictException.class)
	public void updateVariationWithInconsistentCodeTest()
	{
		//given
		final VariationData dto = new VariationData();
		dto.setCode(VARIATION);
		dto.setName(VARIATION_NAME);
		dto.setRank(TWO);
		dto.setStatus(ItemStatus.DISABLED);

		//when
		vcontroller.updateVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, NONEXISTING_VARIATION, dto);
	}

	@Test(expected = NotFoundException.class)
	public void updateNonexistingVariationTest()
	{
		//given
		final VariationData dto = new VariationData();
		dto.setCode(NONEXISTING_VARIATION);
		dto.setName(NONEXISTING_VARIATION_NAME);
		dto.setRank(TWO);
		dto.setStatus(ItemStatus.DISABLED);

		//when
		vcontroller.updateVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, NONEXISTING_VARIATION, dto);
	}

	@Test
	public void deleteVariationTest()
	{
		//when
		vcontroller.deleteVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION);

		//then
		try
		{
			vcontroller.getVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, VARIATION);
			fail("Variation should be deleted");
		}
		catch (final NotFoundException e)
		{
			//ok
		}
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonexisingVariationTest()
	{
		vcontroller.deleteVariation(CATALOG, CATALOG_VERSION, CUSTOMIZATION, NONEXISTING_VARIATION);
	}
}
