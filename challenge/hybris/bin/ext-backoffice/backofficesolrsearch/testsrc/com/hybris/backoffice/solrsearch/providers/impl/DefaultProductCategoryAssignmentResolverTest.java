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
package com.hybris.backoffice.solrsearch.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProductCategoryAssignmentResolverTest
{

	@Spy
	private DefaultProductCategoryAssignmentResolver provider;

	@Test
	public void getIndexedCategoriesShouldReturnOwnAndBaseProductsCategoryAssignments()
	{
		//given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final ProductModel baseProduct = mock(ProductModel.class);

		when(variant.getBaseProduct()).thenReturn(baseProduct);
		final CategoryModel baseCategory = mock(CategoryModel.class);
		when(baseCategory.getCode()).thenReturn("base");
		final CategoryModel subCategory = mock(CategoryModel.class);
		when(subCategory.getCode()).thenReturn("sub");
		when(baseProduct.getSupercategories()).thenReturn(Collections.singleton(baseCategory));
		when(variant.getSupercategories()).thenReturn(Collections.singleton(subCategory));

		//when
		final Set indexedCategories = provider.getIndexedCategories(variant);

		//then
		assertThat(indexedCategories).contains(baseCategory, subCategory);
	}
}
