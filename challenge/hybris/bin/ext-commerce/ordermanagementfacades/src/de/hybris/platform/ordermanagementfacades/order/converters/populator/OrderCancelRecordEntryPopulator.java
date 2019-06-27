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
 */
package de.hybris.platform.ordermanagementfacades.order.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordermanagementfacades.order.cancel.OrderCancelRecordEntryData;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Order Cancel Record Entry Populator
 */

public class OrderCancelRecordEntryPopulator implements Populator<OrderCancelRecordEntryModel, OrderCancelRecordEntryData>
{

	@Override
	public void populate(final OrderCancelRecordEntryModel source, final OrderCancelRecordEntryData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");
		target.setRefusedMessage(source.getRefusedMessage());
		target.setCancelReason(source.getCancelReason());
		target.setCancelResult(source.getCancelResult().getCode());
	}
}
