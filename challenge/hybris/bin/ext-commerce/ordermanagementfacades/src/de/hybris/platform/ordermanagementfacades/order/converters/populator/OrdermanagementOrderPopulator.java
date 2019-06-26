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
package de.hybris.platform.ordermanagementfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.OrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;


/**
 * Ordermanagementfacade populator for converting {@link OrderModel}
 */
public class OrdermanagementOrderPopulator extends OrderPopulator
{
	@Override
	protected void addDeliveryMethod(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final DeliveryModeModel deliveryMode = source.getDeliveryMode();
		if (deliveryMode != null)
		{
			DeliveryModeData deliveryModeData;
			if (deliveryMode instanceof ZoneDeliveryModeModel)
			{
				deliveryModeData = getZoneDeliveryModeConverter().convert((ZoneDeliveryModeModel) deliveryMode);
			}
			else
			{
				deliveryModeData = getDeliveryModeConverter().convert(deliveryMode);
			}
			prototype.setDeliveryMode(deliveryModeData);
		}
	}

	@Override
	protected void addDetails(final OrderModel source, final OrderData target)
	{
		super.addDetails(source, target);
		target.setCreated(source.getCreationtime());
	}
}
