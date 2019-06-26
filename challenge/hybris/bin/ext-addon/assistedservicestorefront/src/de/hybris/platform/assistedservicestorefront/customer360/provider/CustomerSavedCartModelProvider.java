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
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class CustomerSavedCartModelProvider implements FragmentModelProvider<CartData>
{
	private UserService userService;
	private SaveCartFacade saveCartFacade;

	@Override
	public CartData getModel(final Map<String, String> parameters)
	{
		final String listSize = parameters.get("listSize");

		if (StringUtils.isEmpty(listSize))
		{
			throw new IllegalArgumentException(
					"Fragment arguments are not provided for provider [" + CustomerSavedCartModelProvider.class.getName() + "] !");
		}

		int productListSize;

		try
		{
			productListSize = Integer.parseInt(listSize);
		}
		catch (final NumberFormatException formatException)
		{
			throw new IllegalArgumentException("Provided value [" + listSize + "] is not in a valid integer range!",
					formatException);
		}

		final PageableData p = new PageableData();
		p.setPageSize(1);
		final SearchPageData<CartData> savedCarts = getSaveCartFacade().getSavedCartsForCurrentUser(p, null);
		if (CollectionUtils.isNotEmpty(savedCarts.getResults()))
		{
			final CartData latestCart = savedCarts.getResults().get(0);

			final List<OrderEntryData> entries = latestCart.getEntries();
			if (entries.size() > productListSize)
			{
				entries.subList(productListSize, entries.size()).clear();
			}
			return latestCart;
		}
		else
		{
			return null;
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected SaveCartFacade getSaveCartFacade()
	{
		return saveCartFacade;
	}

	@Required
	public void setSaveCartFacade(final SaveCartFacade saveCartFacade)
	{
		this.saveCartFacade = saveCartFacade;
	}
}
