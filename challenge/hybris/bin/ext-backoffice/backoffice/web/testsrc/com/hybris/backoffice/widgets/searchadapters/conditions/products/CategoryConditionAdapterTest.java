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

import static com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData.ORPHANED_SEARCH_CONDITIONS_KEY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;

@RunWith(MockitoJUnitRunner.class)
public class CategoryConditionAdapterTest
{

	private CategoryConditionAdapter categoryConditionAdapter;

	@Before
	public void setup()
	{
		categoryConditionAdapter = new CategoryConditionAdapter();
		categoryConditionAdapter.setCategoryPropertyName(ProductModel.SUPERCATEGORIES);
		categoryConditionAdapter.setOperator(ValueComparisonOperator.CONTAINS);
	}

	@Test
	public void shouldAddConditionsForAllCatalogs()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final CategoryModel category = mock(CategoryModel.class);
		final CategoryModel firstSubcategory = mock(CategoryModel.class);
		final CategoryModel secondSubcategory = mock(CategoryModel.class);
		final PK categoryPk = PK.fromLong(1L);
		final PK firstSubcategoryPk = PK.fromLong(2L);
		final PK secondSubcategoryPk = PK.fromLong(3L);

		given(navigationNode.getData()).willReturn(category);
		given(category.getAllSubcategories()).willReturn(Arrays.asList(firstSubcategory, secondSubcategory));
		given(category.getPk()).willReturn(categoryPk);
		given(firstSubcategory.getPk()).willReturn(firstSubcategoryPk);
		given(secondSubcategory.getPk()).willReturn(secondSubcategoryPk);

		// when
		categoryConditionAdapter.addSearchCondition(searchData, navigationNode);

		// then
		assertThat(searchData.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY)).hasSize(1);
		final SearchConditionDataList searchConditionDataList = (SearchConditionDataList) searchData
				.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY).get(0);
		assertThat(searchConditionDataList.getConditions()).onProperty("value").contains(firstSubcategoryPk, secondSubcategoryPk,
				categoryPk);
		assertThat(searchConditionDataList.getConditions()).onProperty("operator").contains(ValueComparisonOperator.CONTAINS,
				ValueComparisonOperator.CONTAINS, ValueComparisonOperator.CONTAINS);
	}
}
