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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultTopValuesProviderTest
{
	private static final String FACET_NAME_1 = "facet1";
	private static final long FACET_COUNT_1 = 10;
	private static final boolean FACET_SELECTED_1 = false;

	private static final String FACET_NAME_2 = "facet2";
	private static final long FACET_COUNT_2 = 30;
	private static final boolean FACET_SELECTED_2 = false;

	private static final String FACET_NAME_3 = "facet3";
	private static final long FACET_COUNT_3 = 20;
	private static final boolean FACET_SELECTED_3 = false;

	private static final String FACET_NAME_4 = "facet4";
	private static final long FACET_COUNT_4 = 60;
	private static final boolean FACET_SELECTED_4 = false;

	private DefaultTopValuesProvider topValuesProvider;

	@Before
	public void setUp() throws Exception
	{
		topValuesProvider = new DefaultTopValuesProvider();
	}

	@Test
	public void emptyFacetValues() throws FieldValueProviderException
	{
		// given
		final IndexedProperty indexedProperty = new IndexedProperty();
		final List<FacetValue> facetValues = new ArrayList<>();

		// when
		final List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetValues);

		// then
		assertNotNull(topFacetValues);
		assertThat(topFacetValues, empty());
	}

	@Test
	public void facetValuesSizeIsLessThanOrEqualTopFacetCount() throws FieldValueProviderException
	{
		// given
		final IndexedProperty indexedProperty = new IndexedProperty();
		final List<FacetValue> facetValues = createFacetValues();

		topValuesProvider.setTopFacetCount(4);

		// when
		final List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetValues);

		// then
		assertNotNull(topFacetValues);
		assertThat(topFacetValues, empty());
	}

	@Test
	public void facetValuesSizeIsMoreThanTopFacetCount() throws FieldValueProviderException
	{
		// given
		final IndexedProperty indexedProperty = new IndexedProperty();
		final List<FacetValue> facetValues = createFacetValues();

		topValuesProvider.setTopFacetCount(2);

		// when
		final List<FacetValue> topFacetValues = topValuesProvider.getTopValues(indexedProperty, facetValues);

		// then
		assertNotNull(topFacetValues);
		assertEquals(2, topFacetValues.size());

		final FacetValue topFacetValue1 = topFacetValues.get(0);
		assertEquals(FACET_NAME_1, topFacetValue1.getName());
		assertEquals(FACET_COUNT_1, topFacetValue1.getCount());

		final FacetValue topFacetValue2 = topFacetValues.get(1);
		assertEquals(FACET_NAME_2, topFacetValue2.getName());
		assertEquals(FACET_COUNT_2, topFacetValue2.getCount());
	}

	protected List<FacetValue> createFacetValues()
	{
		final List<FacetValue> facetValues = new ArrayList<>();

		facetValues.add(new FacetValue(FACET_NAME_1, FACET_COUNT_1, FACET_SELECTED_1));
		facetValues.add(new FacetValue(FACET_NAME_2, FACET_COUNT_2, FACET_SELECTED_2));
		facetValues.add(new FacetValue(FACET_NAME_3, FACET_COUNT_3, FACET_SELECTED_3));
		facetValues.add(new FacetValue(FACET_NAME_4, FACET_COUNT_4, FACET_SELECTED_4));

		return facetValues;
	}
}
