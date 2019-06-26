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

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
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
public class FlexibleSearchFlexibleSearchClassificationSystemVersionConditionAdapterTest
{

	private FlexibleSearchClassificationSystemVersionConditionAdapter classificationSystemConditionAdapter;

	@Before
	public void setup()
	{
		classificationSystemConditionAdapter = new FlexibleSearchClassificationSystemVersionConditionAdapter();
		classificationSystemConditionAdapter.setOperator(ValueComparisonOperator.CONTAINS);
		classificationSystemConditionAdapter.setClassificationSystemVersionPropertyName(ProductModel.SUPERCATEGORIES);
	}

	@Test
	public void shouldAddConditionsForClassificationSystemVersion()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final ClassificationSystemVersionModel classificationSystemVersion = mock(ClassificationSystemVersionModel.class);
		final ClassificationClassModel rootCategory = mock(ClassificationClassModel.class);
		final ClassificationClassModel firstSubcategory = mock(ClassificationClassModel.class);
		final ClassificationClassModel secondSubcategory = mock(ClassificationClassModel.class);

		final PK categoryPk = PK.fromLong(1L);
		final PK firstSubcategoryPk = PK.fromLong(2L);
		final PK secondSubcategoryPk = PK.fromLong(3L);

		given(navigationNode.getData()).willReturn(classificationSystemVersion);
		given(classificationSystemVersion.getRootCategories()).willReturn(Arrays.asList(rootCategory));
		given(rootCategory.getAllSubcategories()).willReturn(Arrays.asList(firstSubcategory, secondSubcategory));

		given(rootCategory.getPk()).willReturn(categoryPk);
		given(firstSubcategory.getPk()).willReturn(firstSubcategoryPk);
		given(secondSubcategory.getPk()).willReturn(secondSubcategoryPk);

		// when
		classificationSystemConditionAdapter.addSearchCondition(searchData, navigationNode);

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
