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
package de.hybris.platform.acceleratorfacades.ordergridform.impl.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.ordergridform.converters.populators.VariantValueParentCategoryPopulator;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.variants.model.VariantValueCategoryModel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class VariantValueParentCategoryPopulatorTest
{
	private VariantValueParentCategoryPopulator variantValueParentCategoryPopulator;

	@Before
	public void setUp()
	{
		variantValueParentCategoryPopulator = new VariantValueParentCategoryPopulator();
	}

	@Test
	public void testPopulateForVariantValueCategoryModel()
	{
		final CategoryModel categoryModel1 = mock(CategoryModel.class);
		given(categoryModel1.getName()).willReturn("Category1");

		final CategoryModel categoryModel2 = mock(CategoryModel.class);
		given(categoryModel2.getName()).willReturn("Category2");

		final List<CategoryModel> categoryModelList = mock(List.class);
		given(categoryModelList.get(0)).willReturn(categoryModel1);
		given(categoryModelList.get(1)).willReturn(categoryModel2);


		final VariantValueCategoryModel varaintValueCategoryModel = mock(VariantValueCategoryModel.class);
		given(varaintValueCategoryModel.getCode()).willReturn("cat1");
		given(varaintValueCategoryModel.getSequence()).willReturn(Integer.valueOf("2"));
		given(varaintValueCategoryModel.getSupercategories()).willReturn(categoryModelList);

		final CategoryData categoryData = new CategoryData();
		variantValueParentCategoryPopulator.populate(varaintValueCategoryModel, categoryData);

		Assert.assertEquals(categoryData.getSequence(), 2);
		Assert.assertEquals(categoryData.getParentCategoryName(), "Category1");
	}

	@Test
	public void testPopulateForCategoryModel()
	{
		final CategoryModel categoryModel = mock(CategoryModel.class);
		given(categoryModel.getCode()).willReturn("cat1");
		given(categoryModel.getSupercategories()).willReturn(mock(List.class));

		final CategoryData categoryData = mock(CategoryData.class);

		variantValueParentCategoryPopulator.populate(categoryModel, categoryData);
		verify(categoryData, never()).setParentCategoryName(any(String.class));
	}

}
