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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.acceleratorfacades.order.data.PriceRangeData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


public class GroupOrderConsignmentEntryPopulator extends GroupOrderEntryPopulator<OrderModel, OrderData>
{

	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{

		final List<ConsignmentData> consignments = target.getConsignments();
		for (final ConsignmentData consignment : consignments)
		{
			consignment.setEntries(groupConsignmentEntries(consignment.getEntries(), target));
		}

		target.setUnconsignedEntries(super.groupEntries(target.getUnconsignedEntries(), target));
	}

	protected List<ConsignmentEntryData> groupConsignmentEntries(final List<ConsignmentEntryData> entries, final OrderData order)
	{
		final Map<String, ConsignmentEntryData> baseProductGroupConsignmentEntryMap = new HashMap<>();

		final List<ConsignmentEntryData> allConsignmentEntries = new ArrayList<ConsignmentEntryData>();

		boolean anyGroup = false;

		for (final ConsignmentEntryData consignmentEntry : entries)
		{
			final OrderEntryData orderEntry = consignmentEntry.getOrderEntry();
			final ProductData product = orderEntry.getProduct();

			if (isGroupable(product))
			{
				final long quantity = consignmentEntry.getQuantity().longValue();
				final long shippedQuantity = consignmentEntry.getShippedQuantity() == null ?
						0 : consignmentEntry.getShippedQuantity().longValue();
				anyGroup = true;

				ConsignmentEntryData newGroupConsignmentEntry = baseProductGroupConsignmentEntryMap.get(product.getBaseProduct());
				if (newGroupConsignmentEntry == null)
				{
					newGroupConsignmentEntry = new ConsignmentEntryData();
					newGroupConsignmentEntry.setOrderEntry(createGroupedOrderEntry(orderEntry));
					newGroupConsignmentEntry.setQuantity(Long.valueOf(0L));
					newGroupConsignmentEntry.setShippedQuantity(Long.valueOf(0L));

					baseProductGroupConsignmentEntryMap.put(product.getBaseProduct(), newGroupConsignmentEntry);
				}

				final ConsignmentEntryData existingGroupConsignmentEntry = baseProductGroupConsignmentEntryMap.get(product
						.getBaseProduct());
				existingGroupConsignmentEntry.getOrderEntry().getEntries().add(orderEntry);
				existingGroupConsignmentEntry.setQuantity(Long.valueOf(existingGroupConsignmentEntry.getQuantity().longValue()
						+ quantity));
				existingGroupConsignmentEntry.setShippedQuantity(Long.valueOf(existingGroupConsignmentEntry.getShippedQuantity()
						.longValue() + shippedQuantity));
				setEntryGroups(orderEntry, newGroupConsignmentEntry.getOrderEntry(), order);

			}
			else
			{
				allConsignmentEntries.add(consignmentEntry);
			}

		}

		if (anyGroup)
		{
			consolidateGroupedConsignmentOrderEntry(baseProductGroupConsignmentEntryMap);
			allConsignmentEntries.addAll(baseProductGroupConsignmentEntryMap.values());
		}

		return allConsignmentEntries;
	}

	protected void consolidateGroupedConsignmentOrderEntry(
			final Map<String, ConsignmentEntryData> baseProductGroupConsignmentEntryMap)
	{

		for (final String productCode : baseProductGroupConsignmentEntryMap.keySet())
		{
			final OrderEntryData parentEntry = baseProductGroupConsignmentEntryMap.get(productCode).getOrderEntry();
			if (CollectionUtils.isNotEmpty(parentEntry.getEntries()))
			{
				final PriceData firstEntryTotalPrice = parentEntry.getEntries().get(0).getTotalPrice();
				final PriceRangeData priceRange = parentEntry.getProduct().getPriceRange();

				if (firstEntryTotalPrice != null)
				{
					priceRange.setMaxPrice(getMaxPrice(parentEntry, firstEntryTotalPrice));
					priceRange.setMinPrice(getMinPrice(parentEntry, firstEntryTotalPrice));
					parentEntry.setTotalPrice(getTotalPrice(parentEntry, firstEntryTotalPrice));
				}

				parentEntry.setQuantity(getTotalQuantity(parentEntry));
			}

		}
	}
}
