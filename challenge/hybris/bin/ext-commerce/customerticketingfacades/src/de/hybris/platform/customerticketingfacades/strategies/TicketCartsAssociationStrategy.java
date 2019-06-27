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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is responsible list all the customer carts to associate with tickets.
 *
 */
public class TicketCartsAssociationStrategy implements TicketAssociationStrategies
{

	private Converter<CartModel, TicketAssociatedData> ticketAssociationCoverter;

	@Override
	public Map<String, List<TicketAssociatedData>> getObjects(final UserModel currentUser)
	{
		final List<TicketAssociatedData> cartsList = new ArrayList<TicketAssociatedData>(
				Converters.convertAll(currentUser.getCarts(), getTicketAssociationCoverter()));

		final Map<String, List<TicketAssociatedData>> carts = new HashMap<String, List<TicketAssociatedData>>();
		carts.put("Cart", cartsList);


		return cartsList.isEmpty() ? Collections.emptyMap() : carts;
	}

	/**
	 * @return the ticketAssociationCoverter
	 */
	protected Converter<CartModel, TicketAssociatedData> getTicketAssociationCoverter()
	{
		return ticketAssociationCoverter;
	}

	/**
	 * @param ticketAssociationCoverter
	 *           the ticketAssociationCoverter to set
	 */
	@Required
	public void setTicketAssociationCoverter(final Converter<CartModel, TicketAssociatedData> ticketAssociationCoverter)
	{
		this.ticketAssociationCoverter = ticketAssociationCoverter;
	}
}
