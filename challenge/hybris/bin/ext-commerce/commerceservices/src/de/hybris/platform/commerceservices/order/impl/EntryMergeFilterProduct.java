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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.commerceservices.order.EntryMergeFilter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.Objects;

import javax.annotation.Nonnull;


/**
 * Merge only entries with the same product.
 */
public class EntryMergeFilterProduct implements EntryMergeFilter
{
	@Override
	public Boolean apply(@Nonnull final AbstractOrderEntryModel candidate, @Nonnull final AbstractOrderEntryModel target)
	{
		return Boolean.valueOf(Objects.equals(candidate.getProduct(), target.getProduct()));
	}
}
