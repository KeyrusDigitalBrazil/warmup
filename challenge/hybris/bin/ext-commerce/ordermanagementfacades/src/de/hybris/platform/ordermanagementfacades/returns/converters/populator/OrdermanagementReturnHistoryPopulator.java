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
package de.hybris.platform.ordermanagementfacades.returns.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for converting {@link ReturnRequestModel} into {@link ReturnRequestData}, to be used in My Account section of storefront.
 */
public class OrdermanagementReturnHistoryPopulator implements Populator<ReturnRequestModel, ReturnRequestData>
{
	private Converter<OrderModel, OrderData> orderConverter;

	@Override
	public void populate(ReturnRequestModel source, ReturnRequestData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setRma(source.getRMA());
			target.setCode(source.getCode());
			target.setCreationTime(source.getCreationtime());
			target.setStatus(source.getStatus());
			target.setOrder(getOrderConverter().convert(source.getOrder()));
		}
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	@Required
	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}
}
