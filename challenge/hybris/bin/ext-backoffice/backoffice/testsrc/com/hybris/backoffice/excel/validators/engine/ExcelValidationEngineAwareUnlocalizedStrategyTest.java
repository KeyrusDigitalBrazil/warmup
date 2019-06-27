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
package com.hybris.backoffice.excel.validators.engine;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.validation.enums.Severity;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;

import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelValidationEngineAwareUnlocalizedStrategyTest extends AbstractValidationEngineAwareStrategyTest
{

	@InjectMocks
	private ExcelValidationEngineAwareUnlocalizedStrategy strategy;

	@Test
	public void shouldReturnValidationErrorWhenSeveritiesAreEqual()
	{
		// given
		final String validationMessage = "Value cannot be empty";
		strategy.setSeverities(Arrays.asList(Severity.ERROR));
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.getQualifier()).willReturn(ProductModel.CODE);
		final ImportParameters importParameters = prepareImportParameters(StringUtils.EMPTY);
		final HybrisConstraintViolation constraintViolation = prepareConstraintViolation(validationMessage, Severity.ERROR);
		mockValidateValue(ProductModel.CODE, Sets.newHashSet(constraintViolation));

		// when
		final ExcelValidationResult validationResult = strategy.validate(importParameters, excelAttribute);

		// then
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(validationMessage);
	}

	@Test
	public void shouldNotReturnValidationErrorWhenSeverityIsDifferent()
	{
		// given
		final String validationMessage = "Value cannot be empty";
		strategy.setSeverities(Arrays.asList(Severity.ERROR));
		final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
		given(excelAttribute.getQualifier()).willReturn(ProductModel.CODE);
		final ImportParameters importParameters = prepareImportParameters(StringUtils.EMPTY);
		final HybrisConstraintViolation constraintViolation = prepareConstraintViolation(validationMessage, Severity.WARN);
		mockValidateValue(ProductModel.CODE, Sets.newHashSet(constraintViolation));

		// when
		final ExcelValidationResult validationResult = strategy.validate(importParameters, excelAttribute);

		// then
		assertThat(validationResult.getValidationErrors()).isEmpty();
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(ProductModel._TYPECODE, null, cellValue, null, Collections.emptyList());
	}
}
