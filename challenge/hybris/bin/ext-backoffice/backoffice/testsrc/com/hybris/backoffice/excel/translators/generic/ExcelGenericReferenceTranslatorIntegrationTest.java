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
package com.hybris.backoffice.excel.translators.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@Transactional
@IntegrationTest
public class ExcelGenericReferenceTranslatorIntegrationTest extends ServicelayerTest
{

	@Resource
	ExcelGenericReferenceTranslator excelGenericReferenceTranslator;

	@Resource
	TypeService typeService;

	@Before
	public void setup()
	{
		excelGenericReferenceTranslator.setExcludedFields(new ArrayList<>());
	}

	@Test
	public void shouldNotHandleRequestWhenAttributeIsPartOf()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getPartOf()).willReturn(true);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleRequestWhenAttributeTypeIsRelation()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.COMMENTS);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldHandleRequestWhenAttributeTypeIsCollection()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldHandleRequestWhenAttributeTypeIsReferenceType()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleRequestWhenAttributeHasNoUniqueAttributes()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.OWNER);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleRequestWhenAttributeTypeIsPlainComposedType()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(composedTypeModel);
		given(composedTypeModel.getCode()).willReturn(ComposedTypeModel._TYPECODE);

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleRequestWhenAttributeIsOnExcludedList()
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(composedTypeModel);
		given(attributeDescriptor.getQualifier()).willReturn(CategoryModel.PRODUCTS);
		given(composedTypeModel.getCode()).willReturn(CategoryModel._TYPECODE);
		given(attributeDescriptor.getEnclosingType()).willReturn(composedTypeModel);
		excelGenericReferenceTranslator.setExcludedFields(Arrays.asList("Category.products"));

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleRequestWhenAttributeForParentTypeIsOnExcludedList()
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(composedTypeModel);
		given(attributeDescriptor.getQualifier()).willReturn(CategoryModel.PRODUCTS);
		given(composedTypeModel.getCode()).willReturn(ClassificationClassModel._TYPECODE);
		given(attributeDescriptor.getEnclosingType()).willReturn(composedTypeModel);
		excelGenericReferenceTranslator.setExcludedFields(Arrays.asList("Category.products"));

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleRequestWhenAttributeIsNotOnExcludedList()
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(composedTypeModel);
		given(attributeDescriptor.getQualifier()).willReturn(CategoryModel.PRODUCTS);
		given(composedTypeModel.getCode()).willReturn(CategoryModel._TYPECODE);
		given(attributeDescriptor.getEnclosingType()).willReturn(composedTypeModel);
		excelGenericReferenceTranslator.setExcludedFields(Arrays.asList("FakeCategory.products"));

		// when
		final boolean canHandle = excelGenericReferenceTranslator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}
}
