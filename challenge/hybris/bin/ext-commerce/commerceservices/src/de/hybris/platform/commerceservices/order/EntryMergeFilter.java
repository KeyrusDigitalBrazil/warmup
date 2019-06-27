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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;


/**
 * Filter to reject entry merge for particular pair of entries.
 *
 * Second entry (which is going to be added to cart) is presented by {@link CommerceCartParameter}, because it does not
 * exist in cart so far.
 */
public interface EntryMergeFilter extends BiFunction<AbstractOrderEntryModel, AbstractOrderEntryModel, Boolean>
{
	/**
	 * Return {@link Boolean#FALSE} to create the item given by {@code parameter} as a separate cart entry or
	 * {@link Boolean#TRUE} to allow it to be merged into the given entry.</p>
	 *
	 * <p>
	 * Please be aware that allowing to merge does not always means the entries will be merged. There could be another
	 * filter registered, that could deny the merge.
	 * </p>
	 *
	 * @param candidate
	 *           merge candidate
	 * @param target
	 *           entry to merge the candidate into
	 * @return TRUE is the merge is allowed
	 */
	@Override
	Boolean apply(@Nonnull AbstractOrderEntryModel candidate, @Nonnull AbstractOrderEntryModel target);
}
