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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportService;
import com.hybris.backoffice.excel.translators.ExcelMediaImportTranslator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMediaImportValidatorTest
{
	@InjectMocks
	private ExcelMediaImportValidator validator;
	@Mock
	private TypeService typeService;
	@Mock
	private ExcelImportService importService;

	@Before
	public void setUp()
	{
		when(typeService.isAssignableFrom(MediaModel._TYPECODE, MediaModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void shouldHandleMediaType()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "theCode");

		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(MediaModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		assertThat(validator.canHandle(importParameters, attrDesc)).isTrue();
	}

	@Test
	public void shouldNotHandleProductType()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "theCode");

		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(ProductModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		assertThat(validator.canHandle(importParameters, attrDesc)).isFalse();
	}

	@Test
	public void shouldValidateCodeAndPathCannotBeBothEmpty()
	{
		final Map<String, String> params = new HashMap<>();

		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(ProductModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		final ExcelValidationResult validate = validator.validate(importParameters, attrDesc, new HashMap<>());

		assertThat(validate.getValidationErrors()).hasSize(1);
		assertThat(validate.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelMediaImportValidator.VALIDATION_PATH_AND_CODE_EMPTY);

	}

	@Test
	public void shouldValidateZipExists()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "d");
		final HashMap<String, Object> context = new HashMap<>();

		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(ProductModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		final ExcelValidationResult validate = validator.validate(importParameters, attrDesc, context);

		assertThat(validate.getValidationErrors()).hasSize(1);
		assertThat(validate.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelMediaImportValidator.VALIDATION_MISSING_ZIP);
	}

	@Test
	public void shouldValidateFileExists()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "d");
		final HashMap<String, Object> context = new HashMap<>();
		context.put(ExcelValidator.CTX_MEDIA_CONTENT_ENTRIES, Sets.newHashSet("a,b,c"));


		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(ProductModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		final ExcelValidationResult validate = validator.validate(importParameters, attrDesc, context);

		assertThat(validate.getValidationErrors()).hasSize(1);
		assertThat(validate.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelMediaImportValidator.VALIDATION_MISSING_FILE_IN_ZIP);
	}
}
