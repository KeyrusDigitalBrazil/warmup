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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.strategies.PickupAvailabilityStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Product Populator that sets flags for delivery/pickup availability
 */
public class ProductDeliveryModeAvailabilityPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private PickupAvailabilityStrategy pickupAvailabilityStrategy;
	private BaseStoreService baseStoreService;

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected PickupAvailabilityStrategy getPickupAvailabilityStrategy()
	{
		return pickupAvailabilityStrategy;
	}

	@Required
	public void setPickupAvailabilityStrategy(final PickupAvailabilityStrategy pickupAvailabilityStrategy)
	{
		this.pickupAvailabilityStrategy = pickupAvailabilityStrategy;
	}

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setAvailableForPickup(getPickupAvailabilityStrategy().isPickupAvailableForProduct(source,
				getBaseStoreService().getCurrentBaseStore()));
	}
}
