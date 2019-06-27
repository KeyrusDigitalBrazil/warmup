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
package de.hybris.platform.chineselogisticservices.delivery.impl;

import de.hybris.platform.chineselogisticservices.delivery.DeliveryTimeSlotService;
import de.hybris.platform.chineselogisticservices.delivery.dao.DeliveryTimeSlotDao;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 *
 * Chinese specific implementation of {@link DeliveryTimeSlotService}
 *
 */
public class DefaultChineseDeliveryTimeSlotService implements DeliveryTimeSlotService
{
	private DeliveryTimeSlotDao deliveryTimeSlotDao;
	private ModelService modelService;

	@Override
	public List<DeliveryTimeSlotModel> getAllDeliveryTimeSlots()
	{
		return deliveryTimeSlotDao.getAllDeliveryTimeSlots();
	}

	@Override
	public DeliveryTimeSlotModel getDeliveryTimeSlotByCode(final String code)
	{
		return deliveryTimeSlotDao.getDeliveryTimeSlotByCode(code);
	}

	@Override
	public void setDeliveryTimeSlot(final CartModel cartModel, final String deliveryTimeSlot)
	{
		final DeliveryTimeSlotModel deliveryTimeSlotModel = getDeliveryTimeSlotByCode(deliveryTimeSlot);
		cartModel.setDeliveryTimeSlot(deliveryTimeSlotModel);
		modelService.save(cartModel);
	}

	protected DeliveryTimeSlotDao getDeliveryTimeSlotDao()
	{
		return deliveryTimeSlotDao;
	}

	@Required
	public void setDeliveryTimeSlotDao(final DeliveryTimeSlotDao deliveryTimeSlotDao)
	{
		this.deliveryTimeSlotDao = deliveryTimeSlotDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
