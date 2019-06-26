/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SearchAttributeSelectionStrategyImplTest
{
	SearchAttributeSelectionStrategyImpl classUnderTest = new SearchAttributeSelectionStrategyImpl();

	@Mock
	SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;

	@Mock
	SolrFacetSearchConfigModel searchConfig;

	private static final String propertyName = "WEC_DC_COLOR";
	private static final String propertyUnknown = "WEC_UNKNOWN";

	private final Set<String> indexedProperties = new HashSet<>();

	private final List<SolrIndexedTypeModel> indexedTypes = new ArrayList<>();

	@Mock
	private final SolrIndexedTypeModel indexedType = new SolrIndexedTypeModel();

	private final List<SolrIndexedPropertyModel> indexedPropertyList = new ArrayList<>();

	private final SolrIndexedPropertyModel indexedPropertyModel = new SolrIndexedPropertyModel();



	@Before
	public void setup() throws NoValidSolrConfigException
	{
		MockitoAnnotations.initMocks(this);
		indexedTypes.add(indexedType);
		indexedPropertyList.add(indexedPropertyModel);
		indexedPropertyModel.setName(propertyName);
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).thenReturn(searchConfig);
		when(searchConfig.getSolrIndexedTypes()).thenReturn(indexedTypes);
		when(indexedType.getSolrIndexedProperties()).thenReturn(indexedPropertyList);
		indexedProperties.add(propertyName);
		classUnderTest.setSolrFacetSearchConfigSelectionStrategy(solrFacetSearchConfigSelectionStrategy);
	}

	@Test
	public void testSolrFacetSearchConfigSelectionStrategy()
	{
		classUnderTest.setSolrFacetSearchConfigSelectionStrategy(solrFacetSearchConfigSelectionStrategy);
		assertEquals(solrFacetSearchConfigSelectionStrategy, classUnderTest.getSolrFacetSearchConfigSelectionStrategy());
	}

	@Test
	public void testIsPropertyAvailableOnSearchIndex() throws NoValidSolrConfigException
	{
		final boolean available = classUnderTest.isAttributeAvailableOnSearchIndex(propertyName, indexedProperties);
		assertTrue(available);
	}

	@Test
	public void testIsPropertyAvailableOnSearchIndexEmptySolrIndex() throws NoValidSolrConfigException
	{
		final boolean available = classUnderTest.isAttributeAvailableOnSearchIndex(propertyName, new HashSet<>());
		assertFalse(available);
	}

	@Test
	public void testIsPropertyAvailableOnSearchIndexUnknownProperty() throws NoValidSolrConfigException
	{
		final boolean available = classUnderTest.isAttributeAvailableOnSearchIndex(propertyUnknown, indexedProperties);
		assertFalse(available);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsPropertyAvailableOnSearchIndexPropertyIsNull() throws NoValidSolrConfigException
	{
		classUnderTest.isAttributeAvailableOnSearchIndex(null, indexedProperties);
	}


	@Test
	public void testCompileIndexedProperties() throws NoValidSolrConfigException
	{
		final Set<String> compileIndexedProperties = classUnderTest.compileIndexedProperties();
		assertNotNull(compileIndexedProperties);
		assertTrue(compileIndexedProperties.contains(propertyName));
		assertFalse(compileIndexedProperties.contains(propertyUnknown));
	}

	@Test(expected = NullPointerException.class)
	public void testCompileIndexedPropertiesNoConfig() throws NoValidSolrConfigException
	{
		when(solrFacetSearchConfigSelectionStrategy.getCurrentSolrFacetSearchConfig()).thenReturn(null);
		classUnderTest.compileIndexedProperties();
	}

	@Test(expected = NullPointerException.class)
	public void testCompileIndexedPropertiesNoIndexedProperties() throws NoValidSolrConfigException
	{
		when(searchConfig.getSolrIndexedTypes()).thenReturn(null);
		classUnderTest.compileIndexedProperties();
	}

	@Test
	public void testCompileIndexedPropertiesNull() throws NoValidSolrConfigException
	{
		when(indexedType.getSolrIndexedProperties()).thenReturn(null);
		assertTrue(classUnderTest.compileIndexedProperties().isEmpty());
	}
}
