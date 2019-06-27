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

import static com.hybris.backoffice.excel.validators.ExcelMandatoryFieldValidator.VALIDATION_MANDATORY_FIELD_MESSAGE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMandatoryFieldValidatorTest
{

	@Mock
	private CommonI18NService commonI18NService;

	@InjectMocks
	private ExcelMandatoryFieldValidator excelMandatoryFieldValidator;

	@Test
	public void shouldHandleWhenAttributeIsNotOptional()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, null, null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getOptional()).thenReturn(false);

		// when
		final boolean canHandle = excelMandatoryFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldHandleWhenAttributeIsNotOptionalAndItIsLocalizedForCurrentLanguage()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, "en", null, null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final LanguageModel languageModel = Mockito.mock(LanguageModel.class);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getLocalized()).thenReturn(true);
		when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		when(languageModel.getIsocode()).thenReturn("en");

		// when
		final boolean canHandle = excelMandatoryFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenAttributeIsOptional()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, null, null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getOptional()).thenReturn(true);

		// when
		final boolean canHandle = excelMandatoryFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleWhenAttributeIsNotOptionalAndIsNotLocalizedForCurrentLanguage()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, "en", null, null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final LanguageModel languageModel = Mockito.mock(LanguageModel.class);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getLocalized()).thenReturn(true);
		when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		when(languageModel.getIsocode()).thenReturn("fr");

		// when
		final boolean canHandle = excelMandatoryFieldValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenCellIsNotBlank()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notEmptyCell", null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelMandatoryFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isFalse();
		assertThat(validationResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenCellIsBlank()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationResult = excelMandatoryFieldValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationResult.hasErrors()).isTrue();
		assertThat(validationResult.getValidationErrors()).hasSize(1);
		assertThat(validationResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(VALIDATION_MANDATORY_FIELD_MESSAGE_KEY);
		assertThat(validationResult.getValidationErrors().get(0).getParams()).isEmpty();
	}

}
