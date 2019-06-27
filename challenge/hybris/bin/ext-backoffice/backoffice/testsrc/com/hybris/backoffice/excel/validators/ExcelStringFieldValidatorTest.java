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
package com.hybris.backoffice.excel.validators;

import static com.hybris.backoffice.excel.validators.ExcelStringValidator.VALIDATION_INCORRECTTYPE_STRING_MESSAGE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelStringFieldValidatorTest
{

	private final ExcelStringValidator excelStringFieldValidator = new ExcelStringValidator();

	@Test
	public void shouldHandleWhenCellValueIsNotBlankAndAttributeIsString()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notBlank", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(String.class.getCanonicalName());

		// when
		final boolean canHandle = excelStringFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenCellIsEmpty()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(String.class.getCanonicalName());

		// when
		final boolean canHandle = excelStringFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleWhenAttributeIsNotString()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "any value", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Double.class.getCanonicalName());

		// when
		final boolean canHandle = excelStringFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenCellHasStringValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "any value", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelStringFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
		assertThat(validationResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenCellDoesntHaveStringValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, 3.14, null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelStringFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(
				VALIDATION_INCORRECTTYPE_STRING_MESSAGE_KEY);
		assertThat(validationResult.getValidationErrors().get(0).getParams()).containsExactly(importParameters.getCellValue());
	}

}
