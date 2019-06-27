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

import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FacetTopValuesProvider;
import de.hybris.platform.solrfacetsearch.search.FacetValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DefaultTopValuesProvider implements FacetTopValuesProvider
{
	private int topFacetCount = 5;

	protected int getTopFacetCount()
	{
		return topFacetCount;
	}

	// Optional
	public void setTopFacetCount(final int topFacetCount)
	{
		this.topFacetCount = topFacetCount;
	}

	@Override
	public List<FacetValue> getTopValues(final IndexedProperty indexedProperty, final List<FacetValue> facetValues)
	{
		final List<FacetValue> topFacetValues = new ArrayList<>();

		if (facetValues != null)
		{
			for (final FacetValue facetValue : facetValues)
			{
				if (facetValue != null && (topFacetValues.size() < getTopFacetCount() || facetValue.isSelected()))
				{
					topFacetValues.add(facetValue);
				}
			}

			if (topFacetValues.size() >= facetValues.size())
			{
				return Collections.emptyList();
			}
		}

		return topFacetValues;
	}

	/**
	 * @deprecated Since 6.7, this is no longer used.
	 */
	@Deprecated
	public static class FacetValueCountComparator extends AbstractComparator<FacetValue>
	{
		public static final FacetValueCountComparator INSTANCE = new FacetValueCountComparator();

		@Override
		protected int compareInstances(final FacetValue facet1, final FacetValue facet2)
		{
			final long facet1Count = facet1.getCount();
			final long facet2Count = facet2.getCount();
			return facet1Count < facet2Count ? 1 : (facet1Count > facet2Count ? -1 : 0);
		}
	}
}
