/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.ConsignmentEntryPopulator;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;


/**
 * Warehousing Converter for converting {@link ConsignmentEntryModel}
 */
public class WarehousingConsignmentEntryPopulator extends ConsignmentEntryPopulator
{
	@Override
	public void populate(final ConsignmentEntryModel source, final ConsignmentEntryData target)
	{
		if (source != null && target != null)
		{
			target.setQuantityDeclined(source.getQuantityDeclined());
			target.setQuantityPending(source.getQuantityPending());
			target.setQuantityShipped(source.getQuantityShipped());
		}

		super.populate(source, target);
	}
}
