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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class for order converters for bundles. This class is responsible for sorting the order entries by the
 * bundle template they are assigned to.
 *
 * @param <SOURCE> class to populate from
 * @param <TARGET> class to populate to
 */
public abstract class AbstractBundleOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData>
		extends AbstractOrderPopulator<SOURCE, TARGET>
{
	private BundleTemplateService bundleTemplateService;

	/**
	 * This method returns the given order entries sorted by the bundle number and component's position.
	 *
	 * @param entries
	 *           {@link List} of {@link AbstractOrderEntryModel}s to be sorted
	 * @return {@link List} of {@link AbstractOrderEntryModel}s ordered by the bundleNo and component.
	 */
	protected List<OrderEntryData> getSortedEntryListBasedOnBundleAndComponent(final List<OrderEntryData> entries)
	{
		Collections.sort(entries, new OrderComparator());
		return entries;
	}

	/**
	 * Arranges cart entries according to bundles.
	 */
	class OrderComparator implements Comparator<OrderEntryData> // NOSONAR
	{
		@Override
		public int compare(final OrderEntryData arg0, final OrderEntryData arg1)
		{
			// sort standalone items as last items
			if (arg0.getBundleNo() == 0)
			{
				return 1;
			}
			if (arg1.getBundleNo() == 0)
			{
				return -1;
			}

			// first comparing is based on the bundleNo
			final int compare = Integer.valueOf(arg0.getBundleNo()).compareTo(arg1.getBundleNo());
			if (compare != 0)
			{
				return compare;
			}

			// second comparing is based on the sort position of the bundle template
			Integer arg0pos = getPositionForComponent(arg0);
			Integer arg1pos = getPositionForComponent(arg1);
			if (arg0pos != null && arg1pos != null)
			{
				return arg0pos.compareTo(arg1pos);
			}

			return 0;
		}
	}

	protected Integer getPositionForComponent(final OrderEntryData entry)
	{
		if (entry.getComponent() == null)
		{
			return null;
		}

		final BundleTemplateModel component0 = getBundleTemplateService()
				.getBundleTemplateForCode(entry.getComponent().getId(), entry.getComponent().getVersion());
		return getBundleTemplateService().getPositionInParent(component0);
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
