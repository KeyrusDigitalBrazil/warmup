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

import java.time.format.DateTimeParseException;
import java.util.HashMap;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.util.ExcelDateUtils;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelDateClassificationFieldValidatorTest
{

	private static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();
	@Mock
	private ExcelDateUtils excelDateUtils;
	@InjectMocks
	private final ExcelDateClassificationFieldValidator validator = new ExcelDateClassificationFieldValidator();

	@Test
	public void shouldValidatorBeInvokedWhenInputIsOfTypeDate()
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
	public void shouldValidatorNotBeHandledWhenInputIsRangeOfBooleans()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "RANGE[20.05.2018 08:53:31;21.05.2018 08:53:31]",
				null, Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidatorNotBeHandledWhenInputIsMultivaluedBoolean()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(null, null, "20.05.2018 08:53:31,21.05.2018 08:53:31", null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final boolean output = validator.canHandle(mockExcelAttribute(), importParameters);

		// then
		assertFalse(output);
	}

	@Test
	public void shouldValidationSucceedWhenInputIsDate()
	{
		// given
		final String cellValue = "20.05.2018 08:53:31";
		given(excelDateUtils.importDate(cellValue)).willReturn(cellValue);

		final ImportParameters importParameters = new ImportParameters(null, null, cellValue, null,
				Lists.newArrayList(new HashMap<>()));

		// when
		final ExcelValidationResult result = validator.validate(mockExcelAttribute(), importParameters, ANY_CONTEXT);

		// then
		assertFalse(result.hasErrors());
	}

	@Test
	public void shouldValidationFailWhenInputIsNotDate()
	{
		// given

		final String cellValue = "2.05.18 0:3:31";
		given(excelDateUtils.importDate(cellValue)).willThrow(new DateTimeParseException("any", "any", 1));

		final ImportParameters importParameters = new ImportParameters(null, null, cellValue, null,
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
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.DATE);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);
		return attribute;
	}

}
