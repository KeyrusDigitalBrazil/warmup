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

package de.hybris.platform.configurablebundlefacades.order.converters.comparator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;


/**
 * The class of OrderEntryBundleComparator.
 */
public class OrderEntryBundleNoComparator extends AbstractBundleOrderEntryComparator<OrderEntryData>
{
	@Override
	protected int doCompare(OrderEntryData o1, OrderEntryData o2)
	{
		// sort standalone items as last items
		if (bundleNumberIsZero(o1))
		{
			return 1;
		}
		if (bundleNumberIsZero(o2))
		{
			return -1;
		}

		// first comparing based on the bundleNo
		if (o1 != null && o2 != null)
		{
			final int compare = Integer.valueOf(o1.getBundleNo()).compareTo(o2.getBundleNo());
			if (compare != 0)
			{
				return compare;
			}
		}
        
        return 0;
	}

	protected boolean bundleNumberIsZero(OrderEntryData orderEntryData)
	{
		return orderEntryData != null && orderEntryData.getBundleNo() == 0;
	}

	@Override
	public boolean comparable(OrderEntryData o1, OrderEntryData o2)
	{
		return !(o1 == null && o2 == null);
	}
}
