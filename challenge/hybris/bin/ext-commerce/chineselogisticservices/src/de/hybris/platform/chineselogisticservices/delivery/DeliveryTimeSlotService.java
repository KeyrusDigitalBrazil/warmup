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
package de.hybris.platform.chineselogisticservices.delivery;

import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Providing some services about DeliveryTimeSlot
 */
public interface DeliveryTimeSlotService
{
	/**
	 * Getting all of the DeliveryTimeSlots
	 *
	 * @return List<DeliveryTimeSlotModel>
	 */
	List<DeliveryTimeSlotModel> getAllDeliveryTimeSlots();

	/**
	 * Getting the DeliveryTimeSlot by code
	 *
	 * @param code
	 *           the code of the DeliveryTimeSlotModel
	 * @return delivery timeslot model
	 */
	DeliveryTimeSlotModel getDeliveryTimeSlotByCode(String code);

	/**
	 * Setting the DeliveryTimeSlot into the cartmodel
	 *
	 * @param cartModel
	 *           cart model
	 * @param deliveryTimeSlot
	 *           delivery timeslot
	 */
	void setDeliveryTimeSlot(CartModel cartModel, String deliveryTimeSlot);
}
