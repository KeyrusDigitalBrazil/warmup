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
package de.hybris.platform.chineselogisticfacades.delivery.impl;

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.chineselogisticfacades.delivery.DeliveryTimeSlotFacade;
import de.hybris.platform.chineselogisticfacades.delivery.data.DeliveryTimeSlotData;
import de.hybris.platform.chineselogisticservices.delivery.DeliveryTimeSlotService;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import reactor.util.CollectionUtils;


/**
 * 
 * Chinese specific implementation of {@link DeliveryTimeSlotFacade}
 *
 */
public class DefaultChineseDeliveryTimeSlotFacade extends DefaultAcceleratorCheckoutFacade implements DeliveryTimeSlotFacade
{
	private Converter<DeliveryTimeSlotModel, DeliveryTimeSlotData> deliveryTimeSlotConverter;
	private DeliveryTimeSlotService deliveryTimeSlotService;

	@Override
	public List<DeliveryTimeSlotData> getAllDeliveryTimeSlots()
	{
		final List<DeliveryTimeSlotModel> deliveryTimeSlotModels = deliveryTimeSlotService.getAllDeliveryTimeSlots();
		if (!CollectionUtils.isEmpty(deliveryTimeSlotModels))
		{
			return getDeliveryTimeSlotConverter().convertAll(deliveryTimeSlotModels);
		}
		return Collections.emptyList();
	}

	@Override
	public void setDeliveryTimeSlot(final String deliveryTimeSlot)
	{
		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			deliveryTimeSlotService.setDeliveryTimeSlot(cartModel, deliveryTimeSlot);
		}
	}

	protected Converter<DeliveryTimeSlotModel, DeliveryTimeSlotData> getDeliveryTimeSlotConverter()
	{
		return deliveryTimeSlotConverter;
	}

	@Required
	public void setDeliveryTimeSlotConverter(
			final Converter<DeliveryTimeSlotModel, DeliveryTimeSlotData> deliveryTimeSlotConverter)
	{
		this.deliveryTimeSlotConverter = deliveryTimeSlotConverter;
	}

	protected DeliveryTimeSlotService getDeliveryTimeSlotService()
	{
		return deliveryTimeSlotService;
	}

	@Required
	public void setDeliveryTimeSlotService(final DeliveryTimeSlotService deliveryTimeSlotService)
	{
		this.deliveryTimeSlotService = deliveryTimeSlotService;
	}

}
