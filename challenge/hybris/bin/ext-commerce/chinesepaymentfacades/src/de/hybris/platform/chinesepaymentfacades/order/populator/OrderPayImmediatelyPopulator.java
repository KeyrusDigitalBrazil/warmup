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
package de.hybris.platform.chinesepaymentfacades.order.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;

import org.springframework.stereotype.Component;


@Component
public class OrderPayImmediatelyPopulator implements Populator<OrderModel, OrderData>
{

	@Override
	public void populate(final OrderModel source, final OrderData target)
	{

		target.setPaymentStatus(source.getPaymentStatus());
		target.setStatus(source.getStatus());
	}

}
