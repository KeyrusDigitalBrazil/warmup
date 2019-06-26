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
package de.hybris.platform.chineselogisticfacades.delivery.populator;

import de.hybris.platform.chineselogisticfacades.delivery.data.DeliveryTimeSlotData;
import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;

import org.springframework.util.Assert;

/**
 * Populating from OrderModel to OrderData and adding DeliveryTimeSlot into OrderData
 */
public class OrderDeliveryTimeSlotPopulator extends AbstractOrderPopulator<OrderModel, OrderData>
{

	@Override
	public void populate(final OrderModel source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getDeliveryTimeSlot() != null)
		{
			final DeliveryTimeSlotData deliveryTimeSlotData = new DeliveryTimeSlotData();
			deliveryTimeSlotData.setCode(source.getDeliveryTimeSlot().getCode());
			deliveryTimeSlotData.setName(source.getDeliveryTimeSlot().getName());
			target.setDeliveryTimeSlot(deliveryTimeSlotData);
		}
	}
}
