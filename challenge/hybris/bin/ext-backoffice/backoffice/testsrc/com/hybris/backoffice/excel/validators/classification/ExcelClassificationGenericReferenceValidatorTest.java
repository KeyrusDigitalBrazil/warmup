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

import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.NUMBER;
import static de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum.REFERENCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.translators.generic.RequiredAttribute;
import com.hybris.backoffice.excel.translators.generic.factory.RequiredAttributesFactory;
import com.hybris.backoffice.excel.validators.ExcelGenericReferenceValidator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelClassificationGenericReferenceValidatorTest
{
	private static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();

	@Mock
	ExcelGenericReferenceValidator mockedExcelGenericReferenceValidator;

	@Mock
	RequiredAttributesFactory mockedRequiredAttributesFactory;

	@InjectMocks
	ExcelClassificationGenericReferenceValidator excelClassificationGenericReferenceValidator;

	@Test
	public void shouldHandleReferenceAttributesAndNonBlankCellValues()
	{
		// given
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(importParameters.isCellValueNotBlank()).willReturn(true);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(REFERENCE);

		// when
		final boolean result = excelClassificationGenericReferenceValidator.canHandle(excelClassificationAttribute,
				importParameters);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotHandleBlankCellValues()
	{
		// given
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(importParameters.isCellValueNotBlank()).willReturn(false);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(REFERENCE);


		// when
		final boolean result = excelClassificationGenericReferenceValidator.canHandle(excelClassificationAttribute,
				importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotHandleNonReferenceAttributes()
	{
		// given
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);

		given(importParameters.isCellValueNotBlank()).willReturn(true);
		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getAttributeType()).willReturn(NUMBER);

		// when
		final boolean result = excelClassificationGenericReferenceValidator.canHandle(excelClassificationAttribute,
				importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldValidateReference()
	{
		// given
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ComposedTypeModel referenceType = mock(ComposedTypeModel.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final RequiredAttribute requiredAttribute = mock(RequiredAttribute.class);
		final ExcelValidationResult excelValidationResult = mock(ExcelValidationResult.class);

		given(excelClassificationAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getReferenceType()).willReturn(referenceType);
		given(mockedRequiredAttributesFactory.create(referenceType)).willReturn(requiredAttribute);
		given(mockedExcelGenericReferenceValidator.validateRequiredAttribute(any(), any(), any()))
				.willReturn(excelValidationResult);

		// when
		final ExcelValidationResult result = excelClassificationGenericReferenceValidator.validate(excelClassificationAttribute,
				importParameters, ANY_CONTEXT);

		// then
		assertThat(result).isEqualTo(excelValidationResult);
		then(mockedExcelGenericReferenceValidator).should().validateRequiredAttribute(requiredAttribute, importParameters,
				ANY_CONTEXT);
	}
}
