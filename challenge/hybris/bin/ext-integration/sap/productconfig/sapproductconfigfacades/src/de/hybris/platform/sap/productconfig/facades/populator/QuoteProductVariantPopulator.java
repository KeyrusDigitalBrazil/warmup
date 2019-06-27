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

import org.apache.log4j.Logger;


/**
 * Takes care of populating product variant relevant attributes for quotes
 */
public class QuoteProductVariantPopulator extends AbstractOrderProductVariantPopulator implements Populator<QuoteModel, QuoteData>
{
	private static final Logger LOG = Logger.getLogger(CartProductVariantPopulator.class);

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
			populateAbstractOrderData(entry, target.getEntries());
		}

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Variant Populating for cart took " + duration + " ms");
		}
	}

}
