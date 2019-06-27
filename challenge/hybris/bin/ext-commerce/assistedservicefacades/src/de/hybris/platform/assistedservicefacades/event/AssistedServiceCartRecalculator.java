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
package de.hybris.platform.assistedservicefacades.event;

import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticketsystem.events.SessionEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Assisted service event listener for {@link SessionEvent} event. Recalculates cart because there can be specific
 * promotions that are only applicable if the ASM emulation is active.
 */
public class AssistedServiceCartRecalculator extends AbstractEventListener<SessionEvent>
{
	private static final Set<EventType> RECALCULATE_EVENTS = new HashSet<EventType>(Arrays.asList(EventType.START_SESSION_EVENT,
			EventType.END_SESSION_EVENT, EventType.AGENT_LOGOUT));
	private static final Logger LOG = Logger.getLogger(AssistedServiceCartRecalculator.class);
	private CommerceCartService commerceCartService;
	private CartService cartService;

	@Override
	protected void onEvent(final SessionEvent event)
	{
		if (RECALCULATE_EVENTS.contains(event.getEventType()) && cartService.getSessionCart() != null)
		{
			try
			{
				final CommerceCartParameter recalcParam = new CommerceCartParameter();
				recalcParam.setEnableHooks(true);
				recalcParam.setCart(cartService.getSessionCart());
				getCommerceCartService().recalculateCart(recalcParam);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Cart %s has been recalculated", cartService.getSessionCart().toString()));
				}
			}
			catch (final CalculationException e)
			{
				LOG.error(e);
			}
		}
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}