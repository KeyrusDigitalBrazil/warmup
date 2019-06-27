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
package com.hybris.backoffice.widgets.searchadapters.conditions.products;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.navigation.impl.SimpleNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class ProductInCategoryConditionAdapterTest
{

	@Spy
	private ProductInCategoryConditionAdapter adapter;

	@Mock
	private NavigationNode nodeNullData;
	@Mock
	private NavigationNode nodeCategoryData;
	@Mock
	private CategoryModel rootCategory;
	@Mock
	private CategoryModel subCategory1;
	@Mock
	private CategoryModel subCategory2;

	@Before
	public void setUp()
	{
		when(nodeCategoryData.getData()).thenReturn(rootCategory);
		final long pkRoot = System.nanoTime();
		when(rootCategory.getPk()).thenReturn(PK.fromLong(pkRoot));
		when(subCategory1.getPk()).thenReturn(PK.fromLong(pkRoot + 1));
		when(subCategory2.getPk()).thenReturn(PK.fromLong(pkRoot + 2));
		final ArrayList<CategoryModel> allSubCategories = new ArrayList<>();
		allSubCategories.add(subCategory1);
		allSubCategories.add(subCategory2);
		when(rootCategory.getAllSubcategories()).thenReturn(allSubCategories);
	}

	@Test
	public void canHandle()
	{

		assertThat(adapter.canHandle(null)).isFalse();
		assertThat(adapter.canHandle(new SimpleNode(""))).isFalse();
		assertThat(adapter.canHandle(nodeNullData)).isFalse();
		assertThat(adapter.canHandle(nodeCategoryData)).isTrue();
	}

	@Test
	public void addSearchCondition()
	{
		//given
		final AdvancedSearchData advancedSearchData = mock(AdvancedSearchData.class);

		//when
		adapter.addSearchCondition(advancedSearchData, nodeCategoryData);

		//then
		final ArgumentCaptor<FieldType> fieldTypeArgumentCaptor = ArgumentCaptor.forClass(FieldType.class);
		final ArgumentCaptor<Collection> includedCategoriesCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(advancedSearchData).addCondition( //
				fieldTypeArgumentCaptor.capture(), //
				eq(ValueComparisonOperator.IN),//
				includedCategoriesCaptor.capture());

		final FieldType fieldType = fieldTypeArgumentCaptor.getValue();
		final Collection includedCategories = includedCategoriesCaptor.getValue();

		assertThat(fieldType.getName()).isEqualTo(ProductModel.SUPERCATEGORIES);
		assertThat(includedCategories).hasSize(3);
		assertThat(includedCategories).contains(rootCategory.getPk());
	}
}
