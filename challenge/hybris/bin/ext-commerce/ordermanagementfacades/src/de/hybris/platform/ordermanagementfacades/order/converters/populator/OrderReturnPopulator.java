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
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Order Return Populator is responsible of populating Returnable flag
 */
public class OrderReturnPopulator implements Populator<OrderModel, OrderData>
{
	private ReturnService returnService;
	private static final int MINIMUM_RETURNABLE_QTY = 1;

	@Override
	public void populate(final OrderModel source, final OrderData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		if(!source.getEntries().isEmpty())
		{
			final boolean isReturnable = source.getEntries().stream()
					.anyMatch(entry -> getReturnService().isReturnable(source,entry,MINIMUM_RETURNABLE_QTY));
			target.setReturnable(isReturnable);


			final Map<AbstractOrderEntryModel, Long> returnableEntryQuantityMap = getReturnService()
					.getAllReturnableEntries(source);
			returnableEntryQuantityMap.forEach((entry, qty) -> target.getEntries().forEach(orderEntryData ->
			{
				// Case of MultiD product
				if(isMultidimensionalEntry(orderEntryData))
				{
					orderEntryData.getEntries().stream().filter(nestedOrderEntry -> nestedOrderEntry.getEntryNumber().equals(entry.getEntryNumber()))
							.forEach(nestedOrderEntryData -> nestedOrderEntryData.setReturnableQty(qty));
				}
				// Case of non MultiD product
				else
				{
					if (orderEntryData.getEntryNumber().equals(entry.getEntryNumber()))
					{
						orderEntryData.setReturnableQty(qty);
					}
				}
			}));
		}
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

	protected ReturnService getReturnService()
	{
		return returnService;
	}

	@Required
	public void setReturnService(final ReturnService returnService)
	{
		this.returnService = returnService;
	}
}
