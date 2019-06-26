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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.strategies.QuoteCartValidationStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link QuoteCartValidationStrategy}.
 */
public class DefaultQuoteCartValidationStrategy implements QuoteCartValidationStrategy
{

	@Override
	public boolean validate(final AbstractOrderModel source, final AbstractOrderModel target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (ObjectUtils.compare(source.getSubtotal(), target.getSubtotal()) != 0
				|| ObjectUtils.compare(source.getTotalDiscounts(), target.getTotalDiscounts()) != 0)
		{
			return false;
		}

		return compareEntries(source.getEntries(), target.getEntries());
	}

	protected boolean compareEntries(final List<AbstractOrderEntryModel> sourceEntries,
			final List<AbstractOrderEntryModel> targetEntries)
	{
		if (CollectionUtils.size(sourceEntries) != CollectionUtils.size(targetEntries))
		{
			return false;
		}

		for (int i = 0; i < sourceEntries.size(); i++)
		{
			final AbstractOrderEntryModel sourceEntry = sourceEntries.get(i);
			final AbstractOrderEntryModel targetEntry = targetEntries.get(i);

			if (ObjectUtils.compare(sourceEntry.getEntryNumber(), targetEntry.getEntryNumber()) != 0
					|| !StringUtils.equals(sourceEntry.getProduct().getCode(), targetEntry.getProduct().getCode())
					|| ObjectUtils.compare(sourceEntry.getQuantity(), targetEntry.getQuantity()) != 0
					|| ObjectUtils.compare(sourceEntry.getTotalPrice(), targetEntry.getTotalPrice()) != 0)
			{
				return false;
			}
		}

		return true;
	}

}
