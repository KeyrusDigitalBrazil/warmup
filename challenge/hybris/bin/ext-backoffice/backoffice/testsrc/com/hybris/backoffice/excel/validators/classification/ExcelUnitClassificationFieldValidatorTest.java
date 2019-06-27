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

import static com.hybris.backoffice.excel.validators.classification.ExcelUnitClassificationFieldValidator.INVALID_UNIT_MESSAGE_KEY;
import static com.hybris.backoffice.excel.validators.classification.ExcelUnitUtils.UNIT_KEY;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.ExcelAttributeValidator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(MockitoJUnitRunner.class)
public class ExcelUnitClassificationFieldValidatorTest
{
	private static final HashMap<String, Object> ANY_CONTEXT = new HashMap<>();
	@Mock
	ClassificationSystemService mockedClassificationSystemService;
	@InjectMocks
	ExcelUnitClassificationFieldValidator excelUnitClassificationFieldValidator;

	@Before
	public void before()
	{
		excelUnitClassificationFieldValidator.setValidators(Collections.emptyList());
	}

	@Test
	public void shouldHandleAttributesWithUnitAndSingleValue()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final Map<String, String> params = new HashMap<>();
		params.put(UNIT_KEY, "unit");
		given(importParameters.getSingleValueParameters()).willReturn(params);

		// when
		final boolean result = excelUnitClassificationFieldValidator.canHandle(excelAttribute, importParameters);

		// then
		assertThat(result).isTrue();
	}

	@Test
	public void shouldNotHandleMultiValueAttributes()
	{
		// given
		final ExcelClassificationAttribute excelAttributeWithUnit = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareMultiValueImportParameters();


		// when
		final boolean result = excelUnitClassificationFieldValidator.canHandle(excelAttributeWithUnit, importParameters);

		// then
		assertThat(result).isFalse();
	}

	private ImportParameters prepareMultiValueImportParameters()
	{
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.getCellValue()).willReturn(ImportParameters.MULTIVALUE_SEPARATOR);
		return importParameters;
	}

	@Test
	public void shouldNotHandleRanges()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		given(importParameters.getCellValue()).willReturn("RANGE[from;to]");

		// when
		final boolean result = excelUnitClassificationFieldValidator.canHandle(excelAttribute, importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldNotHandleAttributesWithoutUnit()
	{
		// given
		final ClassAttributeAssignmentModel classAttributeAssignment = mock(ClassAttributeAssignmentModel.class);
		final ExcelClassificationAttribute excelAttributeWithoutUnit = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);

		given(excelAttributeWithoutUnit.getAttributeAssignment()).willReturn(classAttributeAssignment);

		// when
		final boolean result = excelUnitClassificationFieldValidator.canHandle(excelAttributeWithoutUnit, importParameters);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldCacheUnits()
	{
		// given
		final String unitType = "unitType";

		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);
		given(systemVersionModel.getCatalog()).willReturn(classificationSystemModel);
		given(classificationSystemModel.getId()).willReturn("catalogId");
		given(systemVersionModel.getVersion()).willReturn("version");

		final ClassificationAttributeUnitModel unitModel = mock(ClassificationAttributeUnitModel.class);
		given(unitModel.getUnitType()).willReturn(unitType);
		given(unitModel.getCode()).willReturn("unitCode");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignmentModel.getUnit()).willReturn(unitModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(systemVersionModel);

		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);

		final ImportParameters importParameters = mock(ImportParameters.class);
		final Map<String, Object> context = new HashMap<>();


		given(mockedClassificationSystemService.getUnitsOfTypeForSystemVersion(systemVersionModel, unitType))
				.willReturn(singletonList(unitModel));

		// when
		excelUnitClassificationFieldValidator.validate(excelAttribute, importParameters, context);

		// then
		assertThat(context).containsOnly(entry("PossibleUnitsOf:catalogId:version:unitType", singletonList("unitCode")));
	}

	@Test
	public void shouldExecuteValidators()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ImportParameters expectedPassedImportParameters = new ImportParameters(null, null, "someValue", null,
				new ArrayList<>());

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeUnitModel classificationAttributeUnitModel = mock(ClassificationAttributeUnitModel.class);
		final ExcelAttributeValidator<ExcelClassificationAttribute> validator = mock(ExcelAttributeValidator.class);
		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);

		final Map<String, String> params = new HashMap<>();
		params.put("value", "someValue");

		given(importParameters.getSingleValueParameters()).willReturn(params);
		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(systemVersionModel);
		given(systemVersionModel.getCatalog()).willReturn(classificationSystemModel);
		given(classificationSystemModel.getId()).willReturn("catalogId");
		given(systemVersionModel.getVersion()).willReturn("version");
		given(classAttributeAssignmentModel.getUnit()).willReturn(classificationAttributeUnitModel);
		given(classificationAttributeUnitModel.getUnitType()).willReturn("unitType");

		given(validator.canHandle(excelAttribute, expectedPassedImportParameters)).willReturn(true);
		given(validator.validate(any(), any(), any())).willReturn(ExcelValidationResult.SUCCESS);

		excelUnitClassificationFieldValidator.setValidators(Collections.singletonList(validator));

		// when
		excelUnitClassificationFieldValidator.validate(excelAttribute, importParameters, ANY_CONTEXT);

		// then
		then(validator).should().validate(excelAttribute, expectedPassedImportParameters, ANY_CONTEXT);
	}

	@Test
	public void shouldAggregateValidatorsMessages()
	{
		// given
		final ImportParameters importParameters = mock(ImportParameters.class);
		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);
		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);
		final ClassificationAttributeUnitModel classificationAttributeUnitModel = mock(ClassificationAttributeUnitModel.class);
		final ExcelAttributeValidator<ExcelClassificationAttribute> firstValidator = mock(ExcelAttributeValidator.class);
		final ExcelAttributeValidator<ExcelClassificationAttribute> secondValidator = mock(ExcelAttributeValidator.class);
		final ValidationMessage firstErrorMessage = new ValidationMessage("firstMessageKey");
		final ValidationMessage secondErrorMessage = new ValidationMessage("secondMessageKey");
		final ValidationMessage thirdErrorMessage = new ValidationMessage(INVALID_UNIT_MESSAGE_KEY, "someUnit", "unitType");
		final Map<String, String> params = new HashMap<>();
		params.put("value", "someValue");
		params.put("unit", "someUnit");

		given(importParameters.getSingleValueParameters()).willReturn(params);
		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(systemVersionModel);
		given(systemVersionModel.getCatalog()).willReturn(classificationSystemModel);
		given(classificationSystemModel.getId()).willReturn("catalogId");
		given(systemVersionModel.getVersion()).willReturn("version");
		given(classAttributeAssignmentModel.getUnit()).willReturn(classificationAttributeUnitModel);
		given(classificationAttributeUnitModel.getUnitType()).willReturn("unitType");
		given(firstValidator.canHandle(any(), any())).willReturn(true);
		given(secondValidator.canHandle(any(), any())).willReturn(true);
		given(firstValidator.validate(any(), any(), any())).willReturn(new ExcelValidationResult(firstErrorMessage));
		given(secondValidator.validate(any(), any(), any())).willReturn(new ExcelValidationResult(secondErrorMessage));

		excelUnitClassificationFieldValidator.setValidators(Arrays.asList(firstValidator, secondValidator));

		// when
		final ExcelValidationResult result = excelUnitClassificationFieldValidator.validate(excelAttribute, importParameters,
				ANY_CONTEXT);

		// then
		assertThat(result).isNotNull();
		assertThat(result.hasErrors()).isTrue();
		assertThat(result.getValidationErrors()).contains(firstErrorMessage, secondErrorMessage, thirdErrorMessage);
	}
}
