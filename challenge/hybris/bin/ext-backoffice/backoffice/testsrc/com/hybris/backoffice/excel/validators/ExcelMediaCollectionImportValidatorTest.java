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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportService;
import com.hybris.backoffice.excel.translators.ExcelMediaImportTranslator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMediaCollectionImportValidatorTest
{
	@InjectMocks
	@Spy
	private ExcelMediaCollectionImportValidator validator;
	@Mock
	private TypeService typeService;
	@Mock
	private ExcelImportService importService;

	@Before
	public void setUp()
	{
		validator.setSingleMediaValidators(Lists.newArrayList(new ExcelMediaImportValidator()));
		when(typeService.isAssignableFrom(MediaModel._TYPECODE, MediaModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void shouldHandleMediaType()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "theCode");

		final AttributeDescriptorModel attrDesc = mockCollectionOfType(MediaModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		assertThat(validator.canHandle(importParameters, attrDesc)).isTrue();
	}

	@Test
	public void shouldNotHandleProductType()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "theCode");

		final AttributeDescriptorModel attrDesc = mockCollectionOfType(ProductModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));

		assertThat(validator.canHandle(importParameters, attrDesc)).isFalse();
	}

	@Test
	public void shouldValidateAllEntries()
	{
		final Map<String, String> params = new HashMap<>();
		final Map<String, String> params2 = new HashMap<>();

		final AttributeDescriptorModel attrDesc = mockCollectionOfType(MediaModel._TYPECODE);
		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params, params2));

		final ExcelValidationResult validate = validator.validate(importParameters, attrDesc, new HashMap<>());

		assertThat(validate.getValidationErrors()).hasSize(2);
		assertThat(validate.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelMediaCollectionImportValidator.VALIDATION_PATH_AND_CODE_EMPTY);
		assertThat(validate.getValidationErrors().get(1).getMessageKey())
				.isEqualTo(ExcelMediaCollectionImportValidator.VALIDATION_PATH_AND_CODE_EMPTY);


	}

	protected AttributeDescriptorModel mockCollectionOfType(final String typecode)
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel collectionType = mock(CollectionTypeModel.class);
		when(collectionType.getCode()).thenReturn("CollectionType");

		when(attributeDescriptor.getAttributeType()).thenReturn(collectionType);

		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn(typecode);
		when(collectionType.getElementType()).thenReturn(typeModel);
		return attributeDescriptor;
	}

}
