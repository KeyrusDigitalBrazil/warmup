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

import java.util.Locale;

import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.daos.DeliveryModeDao;
import de.hybris.platform.warehousing.util.builder.DeliveryModeModelBuilder;
import de.hybris.platform.warehousing.util.builder.PickUpDeliveryModeModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class DeliveryModes extends AbstractItems<DeliveryModeModel>
{
	public static final String CODE_PICKUP = "pickup";
	public static final String CODE_REGULAR = "regular";
	public static final String CODE_STANDARD_SHIPMENT = "standard-shipment";
	public static final String DELIVERY_MODE_SHIPING = "free-standard-shipping";

	private DeliveryModeDao deliveryModeDao;

	public DeliveryModeModel Pickup()
	{
		return getFromCollectionOrSaveAndReturn(() -> getDeliveryModeDao().findDeliveryModesByCode(CODE_PICKUP),
				() -> PickUpDeliveryModeModelBuilder.aModel()
						.withCode(CODE_PICKUP)
						.withActive(Boolean.TRUE)
						.withName("Pickup", Locale.ENGLISH)
						.build());
	}

	public DeliveryModeModel Regular()
	{
		return getFromCollectionOrSaveAndReturn(() -> getDeliveryModeDao().findDeliveryModesByCode(CODE_REGULAR),
				() -> DeliveryModeModelBuilder.aModel()
						.withCode(CODE_REGULAR)
						.withActive(Boolean.TRUE)
						.withName("Regular Delivery", Locale.ENGLISH)
						.build());
	}

	public DeliveryModeModel standardShipment(){

		return getFromCollectionOrSaveAndReturn(() -> getDeliveryModeDao().findDeliveryModesByCode(CODE_STANDARD_SHIPMENT),
				() -> DeliveryModeModelBuilder.aModel()
				.withCode(CODE_STANDARD_SHIPMENT)
				.withName(DELIVERY_MODE_SHIPING, Locale.ENGLISH)
				.withActive(Boolean.TRUE)
				.build());
	}

	public DeliveryModeDao getDeliveryModeDao()
	{
		return deliveryModeDao;
	}

	@Required
	public void setDeliveryModeDao(final DeliveryModeDao deliveryModeDao)
	{
		this.deliveryModeDao = deliveryModeDao;
	}

}
