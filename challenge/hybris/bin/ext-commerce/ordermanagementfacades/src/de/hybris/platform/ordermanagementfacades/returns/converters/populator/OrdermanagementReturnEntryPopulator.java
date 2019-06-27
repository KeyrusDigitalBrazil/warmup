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

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class OrdermanagementReturnEntryPopulator implements Populator<ReturnEntryModel, ReturnEntryData>
{
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(ReturnEntryModel source, ReturnEntryData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setExpectedQuantity(source.getExpectedQuantity());
			target.setReachedDate(source.getReachedDate());
			target.setReceivedQuantity(source.getReceivedQuantity());
			target.setNotes(source.getNotes());
			Assert.notNull(source.getOrderEntry(), "Parameter orderEntry in returnEntry cannot be null.");
			target.setOrderEntry(getOrderEntryConverter().convert(source.getOrderEntry()));
			target.setAction(source.getAction());

			if (source instanceof RefundEntryModel)
			{
				final RefundEntryModel refundSource = (RefundEntryModel) source;
				if(refundSource.getAmount() != null && refundSource.getCurrency() != null){
					target.setRefundAmount(getPriceDataFactory().create(PriceDataType.BUY, refundSource.getAmount(), refundSource.getCurrency()));
				}
				target.setRefundedDate(refundSource.getRefundedDate());
				target.setRefundReason(refundSource.getReason());
			}
		}
	}

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
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
}
