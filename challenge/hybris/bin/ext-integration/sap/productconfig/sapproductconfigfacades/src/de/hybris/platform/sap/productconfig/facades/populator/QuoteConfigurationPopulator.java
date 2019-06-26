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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class QuoteConfigurationPopulator extends AbstractOrderConfigurationPopulator implements Populator<QuoteModel, QuoteData>
{
	private static final Logger LOG = Logger.getLogger(QuoteConfigurationPopulator.class);
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Override
	public void populate(final QuoteModel source, final QuoteData target)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		for (final AbstractOrderEntryModel entry : source.getEntries())
		{
			populateQuoteEntry(entry, target);
		}

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Populating for quote took " + duration + " ms");
		}

	}

	protected void populateQuoteEntry(final AbstractOrderEntryModel entry, final QuoteData target)
	{
		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(entry.getProduct()))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("QuoteItem with PK " + entry.getPk() + " is Configurable ==> populating DTO.");
			}

			writeToTargetEntry(entry, target);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is NOT Configurable ==> skipping population of DTO.");
			}
		}
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	/**
	 * Set helper, to check if the related product is CPQ configurable
	 *
	 * @param cpqConfigurableChecker
	 *           configurator checker
	 */
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

}
