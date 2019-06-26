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
package de.hybris.platform.sap.productconfig.facades.filters;

import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Wrapper class for the list of filters of the product config overview page.
 */
public class OverviewFilterList
{
	private List<ConfigOverviewFilter> filters;

	/**
	 * @return the filters
	 */
	public List<ConfigOverviewFilter> getFilters()
	{
		return Optional.ofNullable(filters)
				.map(List::stream)
				.orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	/**
	 * @param filters
	 *           the filters to set
	 */
	public void setFilters(final List<ConfigOverviewFilter> filters)
	{
		this.filters = Optional.ofNullable(filters)
				.map(List::stream)
				.orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	/**
	 * Provides list of filter objects depending on the given list of filter IDs. <br />
	 *
	 * @param appliedFilterIDs
	 * @return list of filter objects to be applied
	 */
	public List<ConfigOverviewFilter> getAppliedFilters(final List<FilterEnum> appliedFilterIDs)
	{
		final List<ConfigOverviewFilter> appliedFilters = new ArrayList<>();
		for (final ConfigOverviewFilter filter : filters)
		{
			if (filter.isActive(appliedFilterIDs))
			{
				appliedFilters.add(filter);
			}
		}
		return appliedFilters;
	}
}
