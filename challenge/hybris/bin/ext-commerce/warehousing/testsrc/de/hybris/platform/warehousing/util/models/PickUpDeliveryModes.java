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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.commerceservices.delivery.dao.PickupDeliveryModeDao;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.daos.OrderDao;

import org.springframework.beans.factory.annotation.Required;


public class PickUpDeliveryModes extends AbstractItems<DeliveryModeModel>
{
	public static final String CODE_PICKUP = "pickup";
	public static final String DELIVERY_MODE = "pickup";
	public static final String CODE_REGULAR = "regular";
	public static final String CODE_STANDARD_SHIPMENT = "standard-shipment";
	public static final String DELIVERY_MODE_SHIPING = "free-standard-shipping";

	private PickupDeliveryModeDao pickUpDeliveryModeDao;
	private OrderDao orderDao;


	//	public PickUpDeliveryModeModel Pickup()
	//	{
	//		return getFromCollectionOrSaveAndReturn(() -> getPickUpDeliveryModeDao().findDeliveryModesByCode(CODE_PICKUP),
	//				() -> PickUpDeliveryModeModelBuilder.aModel().withCode(CODE_PICKUP).withActive(Boolean.TRUE)
	//						.withName("Pickup", Locale.ENGLISH).build());
	//	}

	public PickupDeliveryModeDao getPickUpDeliveryModeDao()
	{
		return pickUpDeliveryModeDao;
	}

	@Required
	public void setDeliveryModeDao(final PickupDeliveryModeDao pickUpDeliveryModeDao)
	{
		this.pickUpDeliveryModeDao = pickUpDeliveryModeDao;
	}

	public OrderDao getOrderDao()
	{
		return orderDao;
	}

	@Required
	public void setOrderDao(OrderDao orderDao)
	{
		this.orderDao = orderDao;
	}
}
