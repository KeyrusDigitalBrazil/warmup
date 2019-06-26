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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link ProductCategoriesPopulator}
 */
@UnitTest
public class ProductCategoriesPopulatorTest
{
	@Mock
	private Converter<CategoryModel, CategoryData> categoryConverter;
	@Mock
	private CommerceProductService commerceProductService;
	@Mock
	private ModelService modelService;

	private ProductCategoriesPopulator productCategoriesPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		productCategoriesPopulator = new ProductCategoriesPopulator();
		productCategoriesPopulator.setModelService(modelService);
		productCategoriesPopulator.setCategoryConverter(categoryConverter);
		productCategoriesPopulator.setCommerceProductService(commerceProductService);
	}


	@Test
	public void testPopulate()
	{
		final ProductModel source = mock(ProductModel.class);
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);
		final List<CategoryModel> supercategories = new ArrayList<CategoryModel>();
		supercategories.add(category1);
		supercategories.add(category2);
		final List<CategoryData> supercategoriesData = new ArrayList<CategoryData>();
		final CategoryData categoryData1 = mock(CategoryData.class);
		final CategoryData categoryData2 = mock(CategoryData.class);
		supercategoriesData.add(categoryData1);
		supercategoriesData.add(categoryData2);

		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(source)).willReturn(supercategories);
		given(categoryConverter.convertAll(supercategories)).willReturn(Arrays.asList(categoryData1, categoryData2));

		final ProductData result = new ProductData();
		productCategoriesPopulator.populate(source, result);

		Assert.assertEquals(2, result.getCategories().size());
		Assert.assertTrue(result.getCategories().contains(categoryData1));
		Assert.assertTrue(result.getCategories().contains(categoryData2));
	}
}
