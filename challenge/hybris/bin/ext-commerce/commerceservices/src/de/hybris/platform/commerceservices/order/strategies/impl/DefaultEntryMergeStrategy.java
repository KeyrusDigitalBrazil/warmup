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
package de.hybris.platform.commerceservices.order.strategies.impl;

import de.hybris.platform.commerceservices.order.EntryMergeFilter;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;


/**
 * Default implementation of {@link EntryMergeStrategy}.
 * <p>
 *    Any module can register a filter of type {@link EntryMergeFilter}. The filter can return {@code FALSE}
 *    to deny merging particular entry with given {@link CommerceCartParameter}.
 * </p>
 * <p>
 *    Also it is possible to change order of preference for input entries.
 * </p>
 *
 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy
 */
public class DefaultEntryMergeStrategy implements EntryMergeStrategy
{
	private List<EntryMergeFilter> entryMergeFilters = Collections.emptyList();
	private Comparator<AbstractOrderEntryModel> entryModelComparator = (entry1, entry2) -> 0;

	@Override
	public AbstractOrderEntryModel getEntryToMerge(
			final List<AbstractOrderEntryModel> entries,
			@Nonnull final AbstractOrderEntryModel newEntry)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("newEntry", newEntry);

		if (entries == null)
		{
			return null;
		}
		return entries.stream()
				.filter(Objects::nonNull)
				.filter(e -> !newEntry.equals(e))
				.filter(entry -> canMerge(entry, newEntry).booleanValue())
				.sorted(getEntryModelComparator())
				.findFirst()
				.orElse(null);
	}

	/**
	 * This method determines whether given entry can be merged with the given entry creation candidate.
	 *
	 * @param mergeCandidate entry that is supposed to be merge acceptor
	 * @param newEntry entry to find merge target for
	 * @return {@link Boolean#FALSE} to disable merge of {@code newEntry} to {@code mergeCandidate}
	 */
	protected Boolean canMerge(
			@Nonnull final AbstractOrderEntryModel mergeCandidate,
			@Nonnull final AbstractOrderEntryModel newEntry)
	{
		return getEntryMergeFilters().stream()
				.map(filter -> filter.apply(mergeCandidate, newEntry))
				.filter(Boolean.FALSE::equals)
				.findAny()
				.orElse(Boolean.TRUE);
	}

	protected List<EntryMergeFilter> getEntryMergeFilters()
	{
		return entryMergeFilters;
	}

	/**
	 * Filters to reject entities that can not be merged.
	 * <p>
	 *    The filters are applied in their natural order,
	 * 	  so it worth to put the filters that are fast and likely return {@link Boolean#FALSE}
	 *    on top of the list.
	 * 	It will speed up the filtration.
	 * </p>
	 *
	 * @see de.hybris.platform.spring.config.ListMergeDirective
	 *
	 * @param items new list of filters
	 */
	public void setEntryMergeFilters(final List<EntryMergeFilter> items)
	{
		entryMergeFilters = items;
	}

	protected Comparator<AbstractOrderEntryModel> getEntryModelComparator()
	{
		return entryModelComparator;
	}

	/**
	 * The comparator can be overridden to change order of preference for entries.
	 * {@link this#getEntryToMerge(List, CommerceCartParameter)} returns first suitable entry of the resulting list.
	 * <p>
	 *    The default implementation does not change order of entries.
	 * </p>
	 *
	 * @param entryModelComparator new {@code AbstractOrderEntryModel} comparator
	 */
	public void setEntryModelComparator(final Comparator<AbstractOrderEntryModel> entryModelComparator)
	{
		this.entryModelComparator = entryModelComparator;
	}
}
