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
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Do not merge entries that are not updatable.
 */
public class EntryMergeFilterIsEntryUpdatable implements EntryMergeFilter
{
	private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;

	@Override
	public Boolean apply(@Nonnull final AbstractOrderEntryModel candidate, @Nonnull final AbstractOrderEntryModel target)
	{
		return Boolean.valueOf(getEntryOrderChecker().canModify(target));
	}

	protected ModifiableChecker<AbstractOrderEntryModel> getEntryOrderChecker()
	{
		return entryOrderChecker;
	}

	@Required
	public void setEntryOrderChecker(final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker)
	{
		this.entryOrderChecker = entryOrderChecker;
	}
}
