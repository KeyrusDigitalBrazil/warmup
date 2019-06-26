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

import static com.hybris.backoffice.excel.validators.ExcelEnumValidator.VALIDATION_INCORRECTTYPE_ENUMVALUE_MESSAGE_KEY;
import static com.hybris.backoffice.excel.validators.ExcelEnumValidator.VALIDATION_INCORRECTTYPE_ENUM_MESSAGE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelEnumFieldValidatorTest
{
	public static final String CHECK = "check";
	@Mock
	private EnumerationService enumerationService;

	@InjectMocks
	private ExcelEnumValidator excelEnumFieldValidator;

	@Test
	public void shouldHandleWhenCellValueIsNotBlankAndAttributeIsEnum()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notBlank", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final EnumerationMetaTypeModel typeModel = mock(EnumerationMetaTypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		// when
		final boolean canHandle = excelEnumFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenCellIsEmpty()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final EnumerationMetaTypeModel typeModel = mock(EnumerationMetaTypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		// when
		final boolean canHandle = excelEnumFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleWhenAttributeIsNotEnum()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, CHECK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Integer.class.getCanonicalName());

		// when
		final boolean canHandle = excelEnumFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenCellHasEnumValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, CHECK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(ArticleApprovalStatus._TYPECODE);
		final List<HybrisEnumValue> enumValues = new ArrayList<>();
		enumValues.add(ArticleApprovalStatus.CHECK);
		enumValues.add(ArticleApprovalStatus.APPROVED);
		enumValues.add(ArticleApprovalStatus.UNAPPROVED);
		when(enumerationService.getEnumerationValues(ArticleApprovalStatus._TYPECODE)).thenReturn(enumValues);

		// when
		final ExcelValidationResult validationResult = excelEnumFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
		assertThat(validationResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenCellDoesntHaveEnumValue()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "abc", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(ArticleApprovalStatus._TYPECODE);
		final List<HybrisEnumValue> enumValues = new ArrayList<>();
		enumValues.add(ArticleApprovalStatus.CHECK);
		enumValues.add(ArticleApprovalStatus.APPROVED);
		enumValues.add(ArticleApprovalStatus.UNAPPROVED);
		when(enumerationService.getEnumerationValues(ArticleApprovalStatus._TYPECODE)).thenReturn(enumValues);

		// when
		final ExcelValidationResult validationResult = excelEnumFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(
				VALIDATION_INCORRECTTYPE_ENUMVALUE_MESSAGE_KEY);
		assertThat(validationResult.getValidationErrors().get(0).getParams()).containsExactly(importParameters.getCellValue(),
				ArticleApprovalStatus._TYPECODE);
	}

	@Test
	public void shouldReturnValidationErrorWhenEnumTypeDoesntExist()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, CHECK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(ArticleApprovalStatus._TYPECODE);
		doThrow(UnknownIdentifierException.class).when(enumerationService).getEnumerationValues(ArticleApprovalStatus._TYPECODE);

		// when
		final ExcelValidationResult validationResult = excelEnumFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(
				VALIDATION_INCORRECTTYPE_ENUM_MESSAGE_KEY);
		assertThat(validationResult.getValidationErrors().get(0).getParams()).containsExactly(importParameters.getCellValue(),
				ArticleApprovalStatus._TYPECODE);
	}
}
