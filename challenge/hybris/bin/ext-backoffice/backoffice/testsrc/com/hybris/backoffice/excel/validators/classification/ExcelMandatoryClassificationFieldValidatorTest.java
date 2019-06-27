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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


public class ExcelMandatoryClassificationFieldValidatorTest
{

	public static final HashMap<String, Object> VALIDATION_CONTEXT = new HashMap<>();
	private final ExcelMandatoryClassificationFieldValidator validator = new ExcelMandatoryClassificationFieldValidator();

	@Test
	public void shouldHandleAttributeWhenAttributeIsMandatory()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareImportParameters(StringUtils.EMPTY);
		given(attribute.isMandatory()).willReturn(true);
		// when
		final boolean canHandle = validator.canHandle(attribute, importParameters);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleAttributeWhenAttributeIsNotMandatory()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareImportParameters(StringUtils.EMPTY);
		given(attribute.isMandatory()).willReturn(false);
		// when
		final boolean canHandle = validator.canHandle(attribute, importParameters);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldReturnValidationErrorWhenCellValueIsNull()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareImportParameters(null);
		given(attribute.isMandatory()).willReturn(true);

		// when
		final ExcelValidationResult validationResult = validator.validate(attribute, importParameters, VALIDATION_CONTEXT);

		// then
		assertThat(validationResult.getValidationErrors()).hasSize(1);
	}

	@Test
	public void shouldReturnValidationErrorWhenCellValueIsBlank()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("   ");
		given(attribute.isMandatory()).willReturn(true);

		// when
		final ExcelValidationResult validationResult = validator.validate(attribute, importParameters, VALIDATION_CONTEXT);

		// then
		assertThat(validationResult.getValidationErrors()).hasSize(1);
	}

	@Test
	public void shouldNotReturnValidationErrorWhenCellValueIsNotBlank()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ImportParameters importParameters = prepareImportParameters("  abc ");
		given(attribute.isMandatory()).willReturn(true);

		// when
		final ExcelValidationResult validationResult = validator.validate(attribute, importParameters, VALIDATION_CONTEXT);

		// then
		assertThat(validationResult.getValidationErrors()).hasSize(0);
	}

	private ImportParameters prepareImportParameters(final String cellValue)
	{
		return new ImportParameters(StringUtils.EMPTY, null, cellValue, null, new ArrayList<>());
	}
}
