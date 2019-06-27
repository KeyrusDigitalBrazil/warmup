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
import de.hybris.platform.commerceservices.product.ProductConfigurableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Disable to merge complex products.
 */
public class EntryMergeFilterConfigurableProduct implements EntryMergeFilter
{
	private ProductConfigurableChecker productConfigurableChecker;

	@Override
	public Boolean apply(@Nonnull final AbstractOrderEntryModel candidate, @Nonnull final AbstractOrderEntryModel target)
	{
		return Boolean.valueOf(CollectionUtils.isEmpty(target.getProductInfos())
				&& !getProductConfigurableChecker().isProductConfigurable(candidate.getProduct()));
	}

	protected ProductConfigurableChecker getProductConfigurableChecker()
	{
		return productConfigurableChecker;
	}

	@Required
	public void setProductConfigurableChecker(final ProductConfigurableChecker productConfigurableChecker)
	{
		this.productConfigurableChecker = productConfigurableChecker;
	}
}
