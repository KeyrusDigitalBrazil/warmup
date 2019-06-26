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
package de.hybris.platform.configurablebundleservices.order.impl;

import de.hybris.platform.commerceservices.order.EntryMergeFilter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;

import java.util.Objects;
import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Prevents merging of entries with different bundle templates.
 *
 * @see de.hybris.platform.configurablebundleservices.model.BundleTemplateModel
 */
public class EntryMergeFilterBundleTemplate implements EntryMergeFilter
{
	private BundleTemplateService bundleTemplateService;

	@Override public Boolean apply(@Nonnull final AbstractOrderEntryModel candidate, @Nonnull final AbstractOrderEntryModel target)
	{
		final EntryGroup group1 = getBundleTemplateService().getBundleEntryGroup(candidate);
		final EntryGroup group2 = getBundleTemplateService().getBundleEntryGroup(target);
		if (group1 == null && group2 == null)
		{
			return Boolean.TRUE;
		}
		return Boolean.valueOf(group1 != null && group2 != null && Objects.equals(group1.getGroupNumber(), group2.getGroupNumber()));
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
