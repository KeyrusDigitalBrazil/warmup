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
package de.hybris.platform.commerceservices.order.strategies;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.List;

import javax.annotation.Nonnull;


/**
 * Cart entry merge strategy: create new entry vs update an existing one.
 *
 * @see de.hybris.platform.commerceservices.order.strategies.impl.DefaultEntryMergeStrategy
 * @see de.hybris.platform.commerceservices.order.EntryMergeFilter
 * @see de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy
 */
public interface EntryMergeStrategy
{
	/**
	 * Returns cart entry can be updated instead of creation of separate {@code newEntry}.
	 *
	 * @param entries
	 *           list of existing entries
	 * @param newEntry
	 *           the merge candidate (can be an item of {@code entries}
	 * @return merge target ({@code null} if no applicable entries found)
	 */
	AbstractOrderEntryModel getEntryToMerge(List<AbstractOrderEntryModel> entries, @Nonnull AbstractOrderEntryModel newEntry);
}
