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
 *
 */
package de.hybris.platform.ordermanagementfacades.returns.converters.populator;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Return Populator
 */
public class OrdermanagementReturnPopulator implements Populator<ReturnRequestModel, ReturnRequestData>
{
	private Converter<ReturnEntryModel, ReturnEntryData> returnEntryConverter;
	private Converter<OrderModel, OrderData> orderConverter;
	private PriceDataFactory priceDataFactory;
	private List<ReturnStatus> cancellableReturnStatusList;

	@Override
	public void populate(final ReturnRequestModel source, final ReturnRequestData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setCode(source.getCode());
			target.setRma(source.getRMA());
			target.setStatus(source.getStatus());
			target.setRefundDeliveryCost(source.getRefundDeliveryCost());
			target.setCancellable(getCancellableReturnStatusList().contains(source.getStatus()));
			if (source.getReturnLabel() != null)
			{
				target.setReturnLabelDownloadUrl(source.getReturnLabel().getDownloadURL());
			}
			if (source.getOrder() != null)
			{
				final CurrencyModel currency = source.getOrder().getCurrency();

				BigDecimal total = BigDecimal.ZERO;

				if (source.getSubtotal() != null)
				{
					target.setSubtotal(getPriceDataFactory().create(PriceDataType.BUY, source.getSubtotal(), currency));
					total = source.getSubtotal();
				}

				if (source.getOrder().getDeliveryCost() != null)
				{
					target.setDeliveryCost(getPriceDataFactory()
							.create(PriceDataType.BUY, BigDecimal.valueOf(source.getOrder().getDeliveryCost()), currency));

					total = addDeliveryCost(source, target, total);
				}

				target.setTotal(getPriceDataFactory().create(PriceDataType.BUY, total, currency));
				target.setOrder(getOrderConverter().convert(source.getOrder()));

			}

			Assert.notNull(source.getReturnEntries(), "Parameter returnEntries in return cannot be null.");
			target.setReturnEntries(Converters.convertAll(source.getReturnEntries(), getReturnEntryConverter()));
		}
	}

	/**
	 * Adds the delivery cost if present to the given total
	 *
	 * @param source
	 * 		the initial {@link ReturnRequestModel}
	 * @param target
	 * 		the targeted {@link ReturnRequestData}
	 * @param total
	 * 		the current total
	 * @return the total amount including delivery cost
	 */
	protected BigDecimal addDeliveryCost(final ReturnRequestModel source, final ReturnRequestData target, final BigDecimal total)
	{
		BigDecimal totalCost = total;
		if (source.getRefundDeliveryCost())
		{
			totalCost = total.add(target.getDeliveryCost().getValue());
		}
		return totalCost;
	}

	protected Converter<ReturnEntryModel, ReturnEntryData> getReturnEntryConverter()
	{
		return returnEntryConverter;
	}

	@Required
	public void setReturnEntryConverter(final Converter<ReturnEntryModel, ReturnEntryData> returnEntryConverter)
	{
		this.returnEntryConverter = returnEntryConverter;
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

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected List<ReturnStatus> getCancellableReturnStatusList()
	{
		return cancellableReturnStatusList;
	}

	@Required
	public void setCancellableReturnStatusList(final List<ReturnStatus> cancellableReturnStatusList)
	{
		this.cancellableReturnStatusList = cancellableReturnStatusList;
	}
}
