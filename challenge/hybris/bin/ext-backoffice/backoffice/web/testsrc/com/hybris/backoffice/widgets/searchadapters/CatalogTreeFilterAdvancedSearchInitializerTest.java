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
package com.hybris.backoffice.widgets.searchadapters;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.tree.model.CatalogTreeModelPopulator;
import com.hybris.backoffice.tree.model.UncategorizedNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.searchadapters.conditions.SearchConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.AllCatalogsConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.CatalogVersionConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.CategoryConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.FlexibleSearchCatalogConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.FlexibleSearchClassificationSystemConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.FlexibleSearchClassificationSystemVersionConditionAdapter;
import com.hybris.backoffice.widgets.searchadapters.conditions.products.FlexibleSearchUncategorizedConditionAdapter;


public class CatalogTreeFilterAdvancedSearchInitializerTest
{

	@Spy
	private AllCatalogsConditionAdapter allCatalogsConditionAdapter;

	@Spy
	private FlexibleSearchCatalogConditionAdapter catalogConditionAdapter;

	@Spy
	private CatalogVersionConditionAdapter catalogVersionConditionAdapter;

	@Spy
	private CategoryConditionAdapter categoryConditionAdapter;

	@Spy
	private FlexibleSearchClassificationSystemConditionAdapter flexibleSearchClassificationSystemConditionAdapter;

	@Spy
	private FlexibleSearchClassificationSystemVersionConditionAdapter flexibleSearchClassificationSystemVersionConditionAdapter;

	@Spy
	private FlexibleSearchUncategorizedConditionAdapter uncategorizedConditionAdapter;

	@Mock
	private AdvancedSearchData advancedSearchData;

	private final List<SearchConditionAdapter> adapters = new ArrayList<>();

	@InjectMocks
	private CatalogTreeFilterAdvancedSearchInitializer flexibleSearchInitializer;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		uncategorizedConditionAdapter.setConditionsAdapters(adapters);
		adapters.add(allCatalogsConditionAdapter);
		adapters.add(flexibleSearchClassificationSystemVersionConditionAdapter);
		adapters.add(flexibleSearchClassificationSystemConditionAdapter);
		adapters.add(catalogConditionAdapter);
		adapters.add(catalogVersionConditionAdapter);
		adapters.add(categoryConditionAdapter);
		adapters.add(uncategorizedConditionAdapter);
		flexibleSearchInitializer.setConditionsAdapters(adapters);
	}

	@Test
	public void shouldInvokeHandlerForAllCatalogsNode()
	{
		// given
		final NavigationNode allCatalogsNode = mock(NavigationNode.class);
		given(allCatalogsNode.getId()).willReturn(CatalogTreeModelPopulator.ALL_CATALOGS_NODE_ID);

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(allCatalogsNode));

		// then
		verify(allCatalogsConditionAdapter).addSearchCondition(advancedSearchData, allCatalogsNode);
	}

	@Test
	public void shouldInvokeHandlerForUncategorizedNode()
	{
		// given
		final NavigationNode uncategorizedNode = mock(NavigationNode.class);
		given(uncategorizedNode.getId()).willReturn(CatalogTreeModelPopulator.UNCATEGORIZED_PRODUCTS_NODE_ID);
		given(uncategorizedNode.getData()).willReturn(mock(UncategorizedNode.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(uncategorizedNode));

		// then
		verify(uncategorizedConditionAdapter).addSearchCondition(advancedSearchData, uncategorizedNode);
	}

	@Test
	public void shouldInvokeHandlerForCatalogNode()
	{
		// given
		final NavigationNode catalogNode = mock(NavigationNode.class);
		given(catalogNode.getData()).willReturn(mock(CatalogModel.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(catalogNode));

		// then
		verify(catalogConditionAdapter).addSearchCondition(advancedSearchData, catalogNode);
	}

	@Test
	public void shouldInvokeHandlerForCatalogVersionNode()
	{
		// given
		final NavigationNode catalogVersionNode = mock(NavigationNode.class);
		given(catalogVersionNode.getData()).willReturn(mock(CatalogVersionModel.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(catalogVersionNode));

		// then
		verify(catalogVersionConditionAdapter).addSearchCondition(advancedSearchData, catalogVersionNode);
	}

	@Test
	public void shouldInvokeHandlerForCategoryNode()
	{
		// given
		final NavigationNode categoryNode = mock(NavigationNode.class);
		given(categoryNode.getData()).willReturn(mock(CategoryModel.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(categoryNode));

		// then
		verify(categoryConditionAdapter).addSearchCondition(advancedSearchData, categoryNode);
	}

	@Test
	public void shouldInvokeHandlerForClassificationSystemNode()
	{
		// given
		final NavigationNode classificationSystemNode = mock(NavigationNode.class);
		given(classificationSystemNode.getData()).willReturn(mock(ClassificationSystemModel.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(classificationSystemNode));

		// then
		verify(flexibleSearchClassificationSystemConditionAdapter).addSearchCondition(advancedSearchData, classificationSystemNode);
	}

	@Test
	public void shouldInvokeHandlerForClassificationSystemVersionNode()
	{
		// given
		final NavigationNode classificationSystemVersionNode = mock(NavigationNode.class);
		given(classificationSystemVersionNode.getData()).willReturn(mock(ClassificationSystemVersionModel.class));

		// when
		flexibleSearchInitializer.addSearchDataConditions(advancedSearchData, Optional.of(classificationSystemVersionNode));

		// then
		verify(flexibleSearchClassificationSystemVersionConditionAdapter).addSearchCondition(advancedSearchData, classificationSystemVersionNode);
	}
}
