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

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Takes care of populating product variant relevant attributes for order
 */
public class OrderProductVariantPopulator extends AbstractOrderProductVariantPopulator
		implements Populator<AbstractOrderModel, OrderData>
{
	private static final Logger LOG = Logger.getLogger(OrderProductVariantPopulator.class);

	@Override
	public void populate(final AbstractOrderModel source, final OrderData target)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		for (final AbstractOrderEntryModel entry : source.getEntries())
		{
			populateAbstractOrderData(entry, target.getEntries());
			populateAbstractOrderData(entry, target.getUnconsignedEntries());
		}

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("CPQ Variant Populating for order took " + duration + " ms");
		}
	}


	@Override
	protected void writeToTargetEntry(final List<OrderEntryData> targetList, final AbstractOrderEntryModel sourceEntry)
	{
		final OrderEntryData targetEntry = targetList.stream() //
				.filter(entry -> entry.getEntryNumber().equals(sourceEntry.getEntryNumber())) //
				.findFirst() //
				.orElse(null);
		if (targetEntry != null)
		{
			adjustTargetEntryForVariant(sourceEntry, targetEntry);
		}
	}
}
