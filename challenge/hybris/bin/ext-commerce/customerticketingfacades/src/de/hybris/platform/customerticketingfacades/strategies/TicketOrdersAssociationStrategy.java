/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingfacades.strategies;

import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is responsible list all the customer orders to associate with tickets.
 *
 */
public class TicketOrdersAssociationStrategy implements TicketAssociationStrategies
{
	private Converter<OrderModel, TicketAssociatedData> ticketAssociationCoverter;

	@Override
	public Map<String, List<TicketAssociatedData>> getObjects(final UserModel currentUser)
	{
		final List<TicketAssociatedData> orderList = new ArrayList<TicketAssociatedData>(
				Converters.convertAll(currentUser.getOrders(), getTicketAssociationCoverter()));

		final Map<String, List<TicketAssociatedData>> orders = new HashMap<String, List<TicketAssociatedData>>();
		orders.put("Order", orderList);
		return orderList.isEmpty() ? Collections.emptyMap() : orders;
	}

	/**
	 * @return the ticketAssociationCoverter
	 */
	protected Converter<OrderModel, TicketAssociatedData> getTicketAssociationCoverter()
	{
		return ticketAssociationCoverter;
	}

	/**
	 * @param ticketAssociationCoverter
	 *           the ticketAssociationCoverter to set
	 */
	public void setTicketAssociationCoverter(final Converter<OrderModel, TicketAssociatedData> ticketAssociationCoverter)
	{
		this.ticketAssociationCoverter = ticketAssociationCoverter;
	}
}
