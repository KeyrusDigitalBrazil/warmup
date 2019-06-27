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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionDataList;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class FlexibleSearchCatalogConditionAdapterTest
{

	private FlexibleSearchCatalogConditionAdapter catalogConditionAdapter;

	@Before
	public void setup()
	{
		catalogConditionAdapter = new FlexibleSearchCatalogConditionAdapter();
		catalogConditionAdapter.setCatalogVersionPropertyName(ProductModel.CATALOGVERSION);
		catalogConditionAdapter.setOperator(ValueComparisonOperator.EQUALS);
	}

	@Test
	public void shouldAddConditionsForCatalog()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode navigationNode = mock(NavigationNode.class);
		final CatalogModel catalog = mock(CatalogModel.class);
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final PK catalogVersionPK = PK.BIG_PK;

		when(navigationNode.getData()).thenReturn(catalog);
		when(catalog.getCatalogVersions()).thenReturn(Sets.newHashSet(catalogVersion));
		when(catalogVersion.getPk()).thenReturn(catalogVersionPK);

		// when
		catalogConditionAdapter.addSearchCondition(searchData, navigationNode);

		// then
		assertThat(searchData.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY)).hasSize(1);
		final SearchConditionDataList searchConditionDataList = (SearchConditionDataList) searchData
				.getConditions(ORPHANED_SEARCH_CONDITIONS_KEY).get(0);
		final SearchConditionData searchConditionData = searchConditionDataList.getConditions().get(0);
		assertThat(searchConditionData.getFieldType().getName()).isEqualTo(ProductModel.CATALOGVERSION);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);
		assertThat(searchConditionData.getValue()).isEqualTo(catalogVersionPK);
	}

	@Test
	public void shouldAddExcludingConditionWhenCatalogDoesNotContainVersions()
	{
		// given
		final CatalogModel catalog = mock(CatalogModel.class);
		when(catalog.getCatalogVersions()).thenReturn(Collections.emptySet());

		final NavigationNode node = mock(NavigationNode.class);
		when(node.getData()).thenReturn(catalog);
		final AdvancedSearchData searchData = new AdvancedSearchData();

		// when
		catalogConditionAdapter.addSearchCondition(searchData, node);

		// then
		assertThat(searchData.getConditions(ProductModel.CATALOGVERSION)).hasSize(1);
		final SearchConditionData searchConditionData = searchData.getConditions(ProductModel.CATALOGVERSION).get(0);
		assertThat(searchConditionData.getFieldType().getName()).isEqualTo(ProductModel.CATALOGVERSION);
		assertThat(searchConditionData.getOperator()).isEqualTo(ValueComparisonOperator.EQUALS);
		assertThat(searchConditionData.getValue()).isEqualTo(0L);
	}
}
