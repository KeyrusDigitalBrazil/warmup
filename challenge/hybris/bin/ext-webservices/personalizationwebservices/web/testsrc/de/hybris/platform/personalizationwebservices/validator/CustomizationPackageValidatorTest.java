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
package de.hybris.platform.personalizationwebservices.validator;

import static de.hybris.platform.personalizationfacades.customization.CustomizationTestUtils.creteCustomizationData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.data.SegmentTriggerData;


@UnitTest
public class CustomizationPackageValidatorTest
{
	protected final static String CUSTOMIZATION = "customization";
	protected final static String CUSTOMIZATION_ID = "customization";
	protected final static String CUSTOMIZATION_NAME = "customization";
	protected final static String SEGMENT_ID = "segment0";
	protected final static String VARIATION_ID = "variation";
	protected final static String VARIATION_NAME = "variation";
	protected final static String TRIGGER_ID = "trigger";

	protected CustomizationPackageValidator validator = new CustomizationPackageValidator();
	protected CustomizationData customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID, VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
	protected Errors errors = new BeanPropertyBindingResult(customizationData, CUSTOMIZATION);

	@Before
	public void setUp()
	{
		SegmentTriggerDataValidator triggerDataValidator = new SegmentTriggerDataValidator();
		triggerDataValidator.setSegmentValidator(new SegmentDataValidator());

		validator.setTriggerValidator(triggerDataValidator);
		validator.setVariationValidator(new VariationDataValidator());
		validator.setCustomizationValidator(new CustomizationDataValidator());

		customizationData = creteCustomizationData(CUSTOMIZATION_ID, CUSTOMIZATION_NAME, VARIATION_ID, VARIATION_NAME, TRIGGER_ID, SEGMENT_ID);
		errors = new BeanPropertyBindingResult(customizationData, CUSTOMIZATION);
	}

	@Test
	public void validateCustomizationTest()
	{
		//when
		validator.validate(customizationData, errors);

		//then
		assertFalse(errors.hasErrors());
	}

	@Test
	public void validateCustomizationWithoutCodeTest()
	{
		//given
		customizationData.setCode(null);

		//when
		validator.validate(customizationData, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertEquals(1, errors.getFieldErrorCount());
		assertEquals("field.required", errors.getFieldError().getCode());
		assertEquals("code", errors.getFieldError().getField());
	}

	@Test
	public void validateVariationWithoutCodeTest()
	{
		//given
		customizationData.getVariations().get(0).setCode(null);

		//when
		validator.validate(customizationData, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertEquals("field.required", errors.getFieldError().getCode());
		assertEquals("variations[0].code", errors.getFieldError().getField());
	}

	@Test
	public void validateTriggerWithoutSegmentsTest()
	{
		//given
		SegmentTriggerData segmentTrigger = (SegmentTriggerData) customizationData.getVariations().get(0).getTriggers().get(0);
		segmentTrigger.setSegments(null);

		//when
		validator.validate(customizationData, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertEquals("field.required", errors.getFieldError().getCode());
		assertEquals("variations[0].triggers[0].segments", errors.getFieldError().getField());
	}

	@Test
	public void validateSegmentWithoutCodeTest()
	{
		//given
		SegmentTriggerData segmentTrigger = (SegmentTriggerData) customizationData.getVariations().get(0).getTriggers().get(0);
		segmentTrigger.getSegments().get(0).setCode(null);

		//when
		validator.validate(customizationData, errors);

		//then
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertEquals("field.required", errors.getFieldError().getCode());
		assertEquals("variations[0].triggers[0].segments[0].code", errors.getFieldError().getField());
	}
}
