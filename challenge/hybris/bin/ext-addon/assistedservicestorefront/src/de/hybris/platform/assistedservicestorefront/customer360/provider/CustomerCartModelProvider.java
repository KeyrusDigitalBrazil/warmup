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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicestorefront.constants.AssistedservicestorefrontConstants;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class CustomerCartModelProvider implements FragmentModelProvider<CartData>
{
	private CartFacade cartFacade;

	@Override
	public CartData getModel(final Map<String, String> parameters)
	{
		final CartData cartData = cartFacade.getSessionCart();
		final int limit = Config.getInt(AssistedservicestorefrontConstants.AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED,
				AssistedservicestorefrontConstants.AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED_DEFAULT);
		if (cartData.getEntries() != null && cartData.getEntries().size() > limit)
		{
			final List<OrderEntryData> entries = cartData.getEntries();
			entries.subList(limit, entries.size()).clear();
		}
		return cartData;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}
}
