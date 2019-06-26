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


import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelCancelableEntriesStrategy;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Order Cancel Populator that is responsible of populating Cancellable flag and flattening out MultiD lines
 */
public class OrderCancelPopulator implements Populator<OrderModel, OrderData>
{
	private OrderCancelService orderCancelService;
	private UserService userService;
	private OrderCancelCancelableEntriesStrategy cancelableEntriesStrategy;


	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");
		boolean isFullCancellationAllowed = getOrderCancelService()
				.isCancelPossible(source, getUserService().getCurrentUser(), false, false).isAllowed();
		boolean isPartialCancellationAllowed = getOrderCancelService()
				.isCancelPossible(source, getUserService().getCurrentUser(), true, true).isAllowed();
		target.setCancellable(isFullCancellationAllowed || isPartialCancellationAllowed);

		final Map<AbstractOrderEntryModel, Long> cancellableEntryQuantityMap = getCancelableEntriesStrategy()
				.getAllCancelableEntries(source, getUserService().getCurrentUser());
		cancellableEntryQuantityMap.forEach((entry, qty) -> target.getEntries().forEach(orderEntryData ->
		{
			// Case of MultiD product
			if (isMultidimensionalEntry(orderEntryData))
			{
				orderEntryData.getEntries().stream()
						.filter(nestedOrderEntry -> nestedOrderEntry.getEntryNumber().equals(entry.getEntryNumber()))
						.forEach(nestedOrderEntryData -> nestedOrderEntryData.setCancellableQty(qty));
			}
			// Case of non MultiD product
			else
			{
				if (orderEntryData.getEntryNumber().equals(entry.getEntryNumber()))
				{
					orderEntryData.setCancellableQty(qty);
				}
			}
		}));
	}

	/**
	 * Confirms if the given {@link OrderEntryData} is for multidimensional product
	 *
	 * @param orderEntry
	 * 		the given {@link OrderEntryData}
	 * @return true, if the given {@link OrderEntryData} is for multidimensional product
	 */
	protected boolean isMultidimensionalEntry(final OrderEntryData orderEntry)
	{
		return orderEntry.getProduct().getMultidimensional() != null && orderEntry.getProduct().getMultidimensional() && !orderEntry
				.getEntries().isEmpty();
	}


	protected OrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	@Required
	public void setOrderCancelService(OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}

	protected OrderCancelCancelableEntriesStrategy getCancelableEntriesStrategy()
	{
		return cancelableEntriesStrategy;
	}

	@Required
	public void setCancelableEntriesStrategy(OrderCancelCancelableEntriesStrategy cancelableEntriesStrategy)
	{
		this.cancelableEntriesStrategy = cancelableEntriesStrategy;
	}

}
