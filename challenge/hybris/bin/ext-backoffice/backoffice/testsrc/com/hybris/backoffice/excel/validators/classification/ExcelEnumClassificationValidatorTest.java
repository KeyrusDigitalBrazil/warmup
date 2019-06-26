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

import static com.hybris.backoffice.excel.validators.classification.ExcelEnumClassificationValidator.VALIDATION_INCORRECT_TYPE_ENUM_MESSAGE_KEY;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.ENUM;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


public class ExcelEnumClassificationValidatorTest
{

	private static final Map<String, Object> ANY_CONTEXT = Collections.emptyMap();

	ExcelEnumClassificationValidator excelEnumClassificationValidator = new ExcelEnumClassificationValidator();

	@Test
	public void shouldHandleNonBlankCellValueAndEnumAttributeType()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(ENUM);
		given(importParameters.isCellValueNotBlank()).willReturn(true);

		// when
		final boolean result = excelEnumClassificationValidator.canHandle(excelAttribute, importParameters);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotHandleEnumAttributeTypeAsTheCellValueIsBlank()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(ENUM);
		given(importParameters.isCellValueNotBlank()).willReturn(false);

		// when
		final boolean result = excelEnumClassificationValidator.canHandle(excelAttribute, importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotHandleNonBlankCellValueAsAttributeTypeIsNotEnum()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(NUMBER);
		given(importParameters.isCellValueNotBlank()).willReturn(true);

		// when
		final boolean result = excelEnumClassificationValidator.canHandle(excelAttribute, importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldValidateWithSuccessResult()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeValueModel attributeValue1 = mock(ClassificationAttributeValueModel.class);
		final ClassificationAttributeValueModel attributeValue2 = mock(ClassificationAttributeValueModel.class);

		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeValues()).willReturn(Arrays.asList(attributeValue1, attributeValue2));
		given(attributeValue2.getCode()).willReturn("someEnumValue");
		given(importParameters.getCellValue()).willReturn("someEnumValue");

		// when
		final ExcelValidationResult result = excelEnumClassificationValidator.validate(excelAttribute, importParameters,
				ANY_CONTEXT);

		// then
		assertThat(result).isEqualTo(ExcelValidationResult.SUCCESS);
	}

	@Test
	public void shouldValidateWithErrorAsTheImportedEnumDoesNotExist()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeValueModel attributeValue1 = mock(ClassificationAttributeValueModel.class);
		final ClassificationAttributeValueModel attributeValue2 = mock(ClassificationAttributeValueModel.class);
		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);

		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeValues()).willReturn(Arrays.asList(attributeValue1, attributeValue2));
		given(classAttributeAssignmentModel.getClassificationAttribute()).willReturn(classificationAttributeModel);
		given(classificationAttributeModel.getCode()).willReturn("typeName");

		given(attributeValue2.getCode()).willReturn("someEnumValue");
		given(importParameters.getCellValue()).willReturn("someOtherEnumValue");

		// when
		final ExcelValidationResult result = excelEnumClassificationValidator.validate(excelAttribute, importParameters,
				ANY_CONTEXT);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getValidationErrors())
				.contains(new ValidationMessage(VALIDATION_INCORRECT_TYPE_ENUM_MESSAGE_KEY, "someOtherEnumValue", "typeName"));
	}
}
