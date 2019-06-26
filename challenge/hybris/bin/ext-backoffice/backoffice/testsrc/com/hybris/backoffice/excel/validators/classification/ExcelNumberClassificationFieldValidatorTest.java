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
package com.hybris.backoffice.excel.validators.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import java.util.HashMap;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelNumberClassificationFieldValidatorTest
{

	private static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();
	private final ExcelNumberClassificationFieldValidator validator = new ExcelNumberClassificationFieldValidator();

	@Test
	public void shouldValidatorBeInvokedWhenInputIsOfTypeNumber()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "some value", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertTrue(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsRangeOfNumbers()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "RANGE[2;3]", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsMultivaluedNumber()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "2,3", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidationSucceedWhenInputIsBoolean()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "2", null, Lists.newArrayList(new HashMap<>()));

		// when
		final ExcelValidationResult result = validator.validate(mockExcelAttribute(), importParameters, ANY_CONTEXT);

		// then
		assertFalse(result.hasErrors());
	}

	@Test
	public void shouldValidationFailWhenInputIsNotBoolean()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "blabla", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final ExcelValidationResult result = validator.validate(mockExcelAttribute(), importParameters, ANY_CONTEXT);

		// then
		assertTrue(result.hasErrors());
	}

	private ExcelClassificationAttribute mockExcelAttribute()
	{
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);
		return attribute;
	}

}
