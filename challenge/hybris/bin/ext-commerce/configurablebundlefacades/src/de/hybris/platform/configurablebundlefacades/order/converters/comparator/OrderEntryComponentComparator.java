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
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import java.util.List;


/**
 * The class of OrderEntryComponentComparator.
 */
public class OrderEntryComponentComparator extends AbstractBundleOrderEntryComparator<OrderEntryData>
{

	@Override
	protected int doCompare(OrderEntryData o1, OrderEntryData o2)
	{
		Integer o1Pos = null;
		Integer o2Pos = null;
		if (o1 != null && o1.getComponent() != null)
		{
			o1Pos = getPosition(o1.getComponent());
		}

		if (o2 != null && o2.getComponent() != null)
		{
			o2Pos = getPosition(o2.getComponent());
		}

		if (o1Pos != null && o2Pos != null)
		{
			return o1Pos.compareTo(o2Pos);
		}

		return 0;
	}

	protected Integer getPosition(final BundleTemplateData bundleTemplate)
	{
		if (bundleTemplate != null && bundleTemplate.getId() != null && bundleTemplate.getVersion() != null)
		{
			final BundleTemplateModel component = getBundleTemplateService().getBundleTemplateForCode(bundleTemplate.getId(),
					bundleTemplate.getVersion());
			if (component != null)
			{
				return getComponentPosition(component);
			}
		}

		return null;
	}

	@Override
	public boolean comparable(OrderEntryData o1, OrderEntryData o2)
	{
		return o1 != null && o2 != null && o1.getBundleNo() == o2.getBundleNo();
	}

	protected Integer getComponentPosition(final BundleTemplateModel component)
	{
		final List<BundleTemplateModel> leafs = getBundleTemplateService().getLeafComponents(component);
		final int idx = leafs.indexOf(component);
		if (idx == -1)
		{
			// That can take place when the component is a non-leaf one.
			return null;
		}
		return idx;
	}
}
