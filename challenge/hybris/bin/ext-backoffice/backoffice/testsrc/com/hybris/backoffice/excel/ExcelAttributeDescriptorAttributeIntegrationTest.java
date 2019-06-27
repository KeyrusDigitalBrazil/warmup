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
package com.hybris.backoffice.excel;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.Transactional;

import javax.annotation.Resource;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;


@Transactional
@IntegrationTest
public class ExcelAttributeDescriptorAttributeIntegrationTest extends ServicelayerTest
{

	@Resource
	TypeService typeService;

	@Test
	public void shouldHandleCodeOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CODE);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isFalse();
		assertThat(excelAttribute.isLocalized()).isFalse();
		assertThat(excelAttribute.getType()).isEqualTo(String.class.getName());
	}

	@Test
	public void shouldHandleNameOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.NAME);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isFalse();
		assertThat(excelAttribute.isLocalized()).isTrue();
		assertThat(excelAttribute.getType()).isEqualTo(String.class.getName());
	}

	@Test
	public void shouldHandleCatalogVersionOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.CATALOGVERSION);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isFalse();
		assertThat(excelAttribute.isLocalized()).isFalse();
		assertThat(excelAttribute.getType()).isEqualTo(CatalogVersionModel._TYPECODE);
	}

	@Test
	public void shouldHandleThumbnailOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.THUMBNAIL);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isFalse();
		assertThat(excelAttribute.isLocalized()).isFalse();
		assertThat(excelAttribute.getType()).isEqualTo(MediaModel._TYPECODE);
	}

	@Test
	public void shouldHandleSupercategoriesOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.SUPERCATEGORIES);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isTrue();
		assertThat(excelAttribute.isLocalized()).isFalse();
		assertThat(excelAttribute.getType()).isEqualTo(CategoryModel._TYPECODE);
	}

	@Test
	public void shouldHandlePricesOfProduct()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(ProductModel._TYPECODE,
				ProductModel.EUROPE1PRICES);

		// when
		final ExcelAttribute excelAttribute = new ExcelAttributeDescriptorAttribute(attributeDescriptor);

		// then
		assertThat(excelAttribute.isMultiValue()).isTrue();
		assertThat(excelAttribute.isLocalized()).isFalse();
		assertThat(excelAttribute.getType()).isEqualTo(PriceRowModel._TYPECODE);
	}

}
