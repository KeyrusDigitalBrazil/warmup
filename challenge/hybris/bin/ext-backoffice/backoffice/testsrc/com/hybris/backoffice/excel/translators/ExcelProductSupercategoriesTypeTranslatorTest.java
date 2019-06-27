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
package com.hybris.backoffice.excel.translators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;


@RunWith(MockitoJUnitRunner.class)
public class ExcelProductSupercategoriesTypeTranslatorTest
{
	@Mock
	public CatalogTypeService catalogTypeService;

	@Mock
	public TypeService typeService;

	@InjectMocks
	private ExcelProductSupercategoriesTypeTranslator translator;

	@Before
	public void setUp()
	{
		when(catalogTypeService.getCatalogVersionContainerAttribute(any())).thenReturn(ProductModel.CATALOGVERSION);
	}

	@Test
	public void shouldExportDataBeNullSafe()
	{
		// expect
		assertThat(translator.exportData(null).isPresent()).isFalse();
	}

	@Test
	public void shouldExportedDataBeInProperFormat()
	{
		// given
		final String id = "defaultcatalog";
		final String code = "some";
		final String version = "Staged";
		final Collection<CategoryModel> categories = generate(3, code, id, version);

		// when
		final String result = translator.exportData(categories).map(String.class::cast).get();

		// then
		final String expectedResultPart = String.format("%s:%s:%s", code, id, version);
		final String expectedResult = String.format("%s,%s,%s", expectedResultPart, expectedResultPart, expectedResultPart);
		assertThat(result).isEqualTo(expectedResult);
	}

	@Test
	public void shouldGivenTypeBeHandled()
	{
		// given
		final RelationDescriptorModel relationDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relationMetaType = mock(RelationMetaTypeModel.class);
		final ComposedTypeModel productComposedType = mock(ComposedTypeModel.class);
		given(relationMetaType.getCode()).willReturn(ProductModel._CATEGORYPRODUCTRELATION);
		given(relationDescriptor.getRelationType()).willReturn(relationMetaType);
		given(relationDescriptor.getEnclosingType()).willReturn(productComposedType);
		given(productComposedType.getCode()).willReturn(ProductModel._TYPECODE);
		given(typeService.isAssignableFrom(ProductModel._TYPECODE, ProductModel._TYPECODE)).willReturn(true);

		// when
		final boolean canHandle = translator.canHandle(relationDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	private Collection<CategoryModel> generate(final int noOfItems, final String code, final String id, final String version)
	{
		return IntStream.range(0, noOfItems).mapToObj(idx -> {
			final CategoryModel category = mock(CategoryModel.class);
			final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
			final CatalogModel catalog = mock(CatalogModel.class);
			given(catalog.getId()).willReturn(id);
			given(catalogVersion.getVersion()).willReturn(version);
			given(catalogVersion.getCatalog()).willReturn(catalog);

			given(category.getCatalogVersion()).willReturn(catalogVersion);
			given(category.getCode()).willReturn(code);

			return category;
		}).collect(Collectors.toList());
	}

	@Test
	public void shouldImportSupercategories()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(VariantProductModel.SUPERCATEGORIES);
		final List<Map<String, String>> parameters = new ArrayList<>();
		final Map<String, String> firstCategoryParams = new HashMap<>();
		firstCategoryParams.put(CatalogVersionModel.CATALOG, "Clothing");
		firstCategoryParams.put(CatalogVersionModel.VERSION, "Online");
		firstCategoryParams.put(ExcelProductSupercategoriesTypeTranslator.CATEGORY_TOKEN, "First category");
		parameters.add(firstCategoryParams);

		final Map<String, String> secondCategoryParams = new HashMap<>();
		secondCategoryParams.put(CatalogVersionModel.CATALOG, "Default");
		secondCategoryParams.put(CatalogVersionModel.VERSION, "Staged");
		secondCategoryParams.put(ExcelProductSupercategoriesTypeTranslator.CATEGORY_TOKEN, "Second category");
		parameters.add(secondCategoryParams);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, null,
				UUID.randomUUID().toString(), parameters);

		// when
		final ImpexValue impexValue = translator.importValue(attributeDescriptor, importParameters);

		// then
		assertThat(impexValue.getValue()).isEqualTo("First category:Online:Clothing,Second category:Staged:Default");
		assertThat(impexValue.getHeaderValue().getName()).isEqualTo("supercategories(code, catalogVersion(version,catalog(id)))");
	}

	@Test
	public void shouldImportEmptyCellWhenProductDoesntHaveAnySupercategories()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(VariantProductModel.SUPERCATEGORIES);
		final List<Map<String, String>> parameters = new ArrayList<>();
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, null,
				UUID.randomUUID().toString(), parameters);

		// when
		final ImpexValue impexValue = translator.importValue(attributeDescriptor, importParameters);

		// then
		assertThat(impexValue.getValue()).isEqualTo(StringUtils.EMPTY);
		assertThat(impexValue.getHeaderValue().getName()).isEqualTo("supercategories(code, catalogVersion(version,catalog(id)))");
	}
}
