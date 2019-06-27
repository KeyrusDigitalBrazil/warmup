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
package de.hybris.platform.ycommercewebservices.queues.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementData;

import org.springframework.util.Assert;


/**
 * Class populate information from OrderModel to OrderStatusUpdateElementData
 */
public class OrderStatusUpdateElementPopulator implements Populator<OrderModel, OrderStatusUpdateElementData>
{
	@Override
	public void populate(final OrderModel source, final OrderStatusUpdateElementData target) throws ConversionException //NOSONAR
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		if (source.getStatus() != null)
		{
			target.setStatus(source.getStatus().getCode());
		}
		if (source.getSite() != null)
		{
			target.setBaseSiteId(source.getSite().getUid());
		}
	}
}
