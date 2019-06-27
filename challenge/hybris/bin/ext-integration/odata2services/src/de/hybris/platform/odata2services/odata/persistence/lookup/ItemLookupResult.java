/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence.lookup;

import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Result of looking up items by the {@link ItemLookupStrategy}. This result is immutable except it does not guarantee immutability
 * of the items returned by {@link #getEntries()} method.
 */
public class ItemLookupResult<T>
{
	private final List<T> entries;
	private final int totalCount;

	private ItemLookupResult(final List<T> entries, final int totalCount)
	{
		this.entries = Collections.unmodifiableList(entries);
		this.totalCount = totalCount;
	}

	public static <T> ItemLookupResult<T> createFrom(final SearchResult<T> result)
	{
		return createFrom(result.getResult(), result.getTotalCount());
	}

	public static <T> ItemLookupResult<T> createFrom(final List<T> entries, final int totalCount)
	{
		return new ItemLookupResult<>(entries, totalCount);
	}

	public static <T> ItemLookupResult<T> createFrom(final List<T> entries)
	{
		return new ItemLookupResult<>(entries, -1);
	}

	/**
	 * Retrieves items existing in the platform and matching the request conditions.
	 * @return all items matching the {@link de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest} conditions or
	 * a subset of all items in case of a paged request. When no items match the conditions, an empty list is returned.
	 */
	public List<T> getEntries()
	{
		return entries;
	}

	/**
	 * Retrieves total number of items matching the request conditions and existing in the platform
	 * @return number of items matching the conditions in the platform. This number may be greater than the number of items returned
	 * by {@link #getEntries()} method. Negative number indicates that total number of matching items is not calculated/unknown.
	 */
	public int getTotalCount()
	{
		return totalCount;
	}

	/**
	 * Transforms this result into a result containing different kind of entries.
	 * @param f a function to be applied to each entry contained in this result
	 * @param <R> type of the entries in the transformed result
	 * @return a result containing entries, which were transformed from the original result by applying the specifed function.
	 */
	public <R> ItemLookupResult<R> map(final Function<T, R> f)
	{
		final List<R> mapped = getEntries().stream()
				.map(f)
				.collect(Collectors.toList());
		return ItemLookupResult.createFrom(mapped, getTotalCount());
	}
}
