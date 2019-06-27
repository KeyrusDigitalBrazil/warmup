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
package de.hybris.platform.acceleratorfacades.cart.action.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandlerRegistry;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates supported actions for a cart entry
 */
public class AcceleratorCartEntryActionPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData>
{
	private CartEntryActionHandlerRegistry cartEntryActionHandlerRegistry;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (source instanceof CartEntryModel)
		{
			final Set<String> supportedActions = new HashSet<>();
			for (final CartEntryAction action : CartEntryAction.values())
			{
				final CartEntryActionHandler handler = getCartEntryActionHandlerRegistry().getHandler(action);
				if (handler != null && handler.supports((CartEntryModel) source))
				{
					supportedActions.add(action.toString());
				}
			}
			target.setSupportedActions(supportedActions);
		}
	}

	protected CartEntryActionHandlerRegistry getCartEntryActionHandlerRegistry()
	{
		return cartEntryActionHandlerRegistry;
	}

	@Required
	public void setCartEntryActionHandlerRegistry(final CartEntryActionHandlerRegistry cartEntryActionHandlerRegistry)
	{
		this.cartEntryActionHandlerRegistry = cartEntryActionHandlerRegistry;
	}

}
