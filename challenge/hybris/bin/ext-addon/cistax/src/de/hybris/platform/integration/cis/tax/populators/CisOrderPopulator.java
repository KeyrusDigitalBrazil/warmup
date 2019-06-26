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
package de.hybris.platform.integration.cis.tax.populators;


import java.util.ArrayList;
import java.util.List;
import com.hybris.cis.client.shared.models.CisLineItem;
import com.hybris.cis.client.shared.models.CisOrder;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.strategies.CisShippingAddressStrategy;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandPromotionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator from {@link AbstractOrderModel} to {@link CisOrder}
 */
public class CisOrderPopulator implements Populator<AbstractOrderModel, CisOrder>
{
	private CisShippingAddressStrategy cisShippingAddressStrategy;
	private OndemandPromotionService ondemandPromotionService;
	private Converter<OndemandDiscountedOrderEntry, CisLineItem> cisLineItemConverter;
	private Converter<AbstractOrderModel, CisLineItem> deliveryCisLineItemConverter;

	@Override
	public void populate(final AbstractOrderModel source, final CisOrder target) throws ConversionException
	{
		if (source == null)
		{
			throw new ConversionException("No order supplied for conversion");
		}
		target.setAddresses(getCisShippingAddressStrategy().getAddresses(source));
		final List<CisLineItem> lineItems = new ArrayList<CisLineItem>(Converters
				.convertAll(getOndemandPromotionService().calculateProportionalDiscountForEntries(source),
						getCisLineItemConverter()));
		if (source.getDeliveryMode() != null)
		{
			lineItems.add(getDeliveryCisLineItemConverter().convert(source));
		}
		target.setLineItems(lineItems);
		target.setId(source.getCode());
		target.setCurrency(source.getCurrency().getIsocode());
		target.setDate(source.getDate());
	}

	protected CisShippingAddressStrategy getCisShippingAddressStrategy()
	{
		return cisShippingAddressStrategy;
	}

	@Required
	public void setCisShippingAddressStrategy(final CisShippingAddressStrategy cisShippingAddressStrategy)
	{
		this.cisShippingAddressStrategy = cisShippingAddressStrategy;
	}

	protected Converter<OndemandDiscountedOrderEntry, CisLineItem> getCisLineItemConverter()
	{
		return cisLineItemConverter;
	}

	@Required
	public void setCisLineItemConverter(final Converter<OndemandDiscountedOrderEntry, CisLineItem> cisLineItemConverter)
	{
		this.cisLineItemConverter = cisLineItemConverter;
	}

	protected Converter<AbstractOrderModel, CisLineItem> getDeliveryCisLineItemConverter()
	{
		return deliveryCisLineItemConverter;
	}

	@Required
	public void setDeliveryCisLineItemConverter(final Converter<AbstractOrderModel, CisLineItem> deliveryCisLineItemConverter)
	{
		this.deliveryCisLineItemConverter = deliveryCisLineItemConverter;
	}

	protected OndemandPromotionService getOndemandPromotionService()
	{
		return ondemandPromotionService;
	}

	@Required
	public void setOndemandPromotionService(OndemandPromotionService ondemandPromotionService)
	{
		this.ondemandPromotionService = ondemandPromotionService;
	}
}
