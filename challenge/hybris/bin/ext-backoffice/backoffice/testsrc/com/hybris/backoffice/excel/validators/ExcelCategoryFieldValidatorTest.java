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

import static com.hybris.backoffice.excel.validators.ExcelCategoryValidator.VALIDATION_CATEGORY_DOESNT_MATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.translators.ExcelProductSupercategoriesTypeTranslator;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelCategoryFieldValidatorTest
{

	public static final String FIRST_CATALOG = "firstCatalog";
	public static final String FIRST_VERSION = "firstVersion";
	public static final String FIRST_CATEGORY = "firstCategory";
	public static final String SECOND_CATEGORY = "secondCategory";
	public static final String NOT_EXISTING_CATEGORY = "not existing category";
	public static final String NOT_BLANK = "notBlank";

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private ExcelCategoryValidator excelCategoryValidator;

	@Before
	public void setup()
	{
		final CategoryModel firstCategory = mock(CategoryModel.class);
		final CategoryModel secondCategory = mock(CategoryModel.class);
		final CatalogVersionModel firstCatalogVersion = mock(CatalogVersionModel.class);
		given(catalogVersionService.getCatalogVersion(FIRST_CATALOG, FIRST_VERSION)).willReturn(firstCatalogVersion);
		given(categoryService.getCategoryForCode(firstCatalogVersion, FIRST_CATEGORY)).willReturn(firstCategory);
		given(categoryService.getCategoryForCode(firstCatalogVersion, SECOND_CATEGORY)).willReturn(secondCategory);
	}

	@Test
	public void shouldHandleCategoryWhenAttributeIsCategoryProductRelation()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				new ArrayList<>());
		final RelationDescriptorModel attributeDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaTypeModel = mock(RelationMetaTypeModel.class);
		given(attributeDescriptor.getRelationType()).willReturn(relationMetaTypeModel);
		given(relationMetaTypeModel.getCode()).willReturn(ProductModel._CATEGORYPRODUCTRELATION);

		// when
		final boolean canHandle = excelCategoryValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleCategoryWhenCellIsEmpty()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final RelationDescriptorModel attributeDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaTypeModel = mock(RelationMetaTypeModel.class);
		given(attributeDescriptor.getRelationType()).willReturn(relationMetaTypeModel);
		given(relationMetaTypeModel.getCode()).willReturn(ProductModel._CATEGORYPRODUCTRELATION);

		// when
		final boolean canHandle = excelCategoryValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleCategoryWhenAttributeIsNotCategoryProductRelation()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				new ArrayList<>());
		final RelationDescriptorModel attributeDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaTypeModel = mock(RelationMetaTypeModel.class);
		given(attributeDescriptor.getRelationType()).willReturn(relationMetaTypeModel);
		given(relationMetaTypeModel.getCode()).willReturn(ProductModel._PRODUCT2KEYWORDRELATION);

		// when
		final boolean canHandle = excelCategoryValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleCategoryWhenAttributeIsNotRelationDescriptor()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final boolean canHandle = excelCategoryValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnErrorWhenCategoryExists()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> singleParams = new HashMap<>();
		parametersList.add(singleParams);
		singleParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		singleParams.put(CatalogVersionModel.VERSION, FIRST_VERSION);
		singleParams.put(ExcelProductSupercategoriesTypeTranslator.CATEGORY_TOKEN, FIRST_CATEGORY);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationCellResult = excelCategoryValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
		assertThat(validationCellResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnErrorWhenCategoryDoesNotExist()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> singleParams = new HashMap<>();
		parametersList.add(singleParams);
		singleParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		singleParams.put(CatalogVersionModel.VERSION, FIRST_VERSION);
		singleParams.put(ExcelProductSupercategoriesTypeTranslator.CATEGORY_TOKEN, NOT_EXISTING_CATEGORY);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		final ExcelValidationResult validationCellResult = excelCategoryValidator.validate(importParameters, attributeDescriptor,
				new HashMap<>());

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(VALIDATION_CATEGORY_DOESNT_MATCH);
	}

}
