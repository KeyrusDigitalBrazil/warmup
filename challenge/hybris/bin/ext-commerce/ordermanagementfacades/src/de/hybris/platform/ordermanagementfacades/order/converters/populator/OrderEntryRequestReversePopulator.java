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

package de.hybris.platform.ordermanagementfacades.order.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.ordermanagementfacades.order.data.OrderEntryRequestData;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Ordermanagementfacade populator for converting {@link OrderEntryRequestData}
 */
public class OrderEntryRequestReversePopulator implements Populator<OrderEntryRequestData, OrderEntryModel>
{
	private DeliveryModeService deliveryModeService;
	private UnitService unitService;
	private PointOfServiceService pointOfServiceService;

	@Override
	public void populate(final OrderEntryRequestData source, final OrderEntryModel target) throws ConversionException
	{
		if (source != null && target != null)
		{
			addCommon(source, target);
			addTotals(source, target);
			addDeliveryMode(source.getDeliveryModeCode(), target);
			if (source.getDeliveryPointOfService() != null)
			{
				addDeliveryPointOfService(source.getDeliveryPointOfService(), target);
			}
		}
	}

	/**
	 * Extracts {@link OrderEntryModel#DELIVERYMODE} from the passed deliveryModeCode and assigns it to the {@link OrderEntryModel}
	 *
	 * @param deliveryModeCode
	 * 		the string equivalent of {@link de.hybris.platform.core.model.order.delivery.DeliveryModeModel#CODE}
	 * @param target
	 * 		the {@link OrderEntryModel}, to which this deliveryMode is assigned
	 */
	protected void addDeliveryMode(final String deliveryModeCode, final OrderEntryModel target)
	{
		if (deliveryModeCode != null)
		{
			target.setDeliveryMode(getDeliveryModeService().getDeliveryModeForCode(deliveryModeCode));
		}
	}

	/**
	 * Extracts {@link OrderEntryModel#TOTALPRICE} & {@link OrderEntryModel#BASEPRICE} from t{@link OrderEntryRequestData}, and
	 * assign it to the {@link OrderEntryModel}.
	 *
	 * @param source
	 * 		the {@link OrderEntryRequestData}
	 * @param target
	 * 		the {@link OrderEntryModel}
	 */
	protected void addTotals(final OrderEntryRequestData source, final OrderEntryModel target)
	{
		target.setBasePrice(source.getBasePrice());
		target.setTotalPrice(source.getTotalPrice());
	}

	/**
	 * Extracts {@link OrderEntryModel#DELIVERYPOINTOFSERVICE} from t{@link OrderEntryRequestData}, and
	 * assign it to the {@link OrderEntryModel}.
	 *
	 * @param deliveryPointOfService
	 * 		the string equivalent of {@link PointOfServiceModel#NAME}
	 * @param target
	 * 		the {@link OrderEntryModel}
	 */
	protected void addDeliveryPointOfService(final String deliveryPointOfService, final OrderEntryModel target)
	{
		final PointOfServiceModel result = getPointOfServiceService().getPointOfServiceForName(deliveryPointOfService);
		target.setDeliveryPointOfService(result);
	}

	/**
	 * Converts the basic properties of the {@link OrderEntryRequestData}
	 *
	 * @param source
	 * 		the {@link OrderEntryRequestData} to be converted
	 * @param target
	 * 		the converted {@link OrderEntryModel} from {@link OrderEntryRequestData}
	 */
	protected void addCommon(final OrderEntryRequestData source, final OrderEntryModel target)
	{
		target.setEntryNumber(source.getEntryNumber());
		target.setQuantity(source.getQuantity());
		target.setUnit(getUnitService().getUnitForCode(source.getUnitCode()));
	}

	protected DeliveryModeService getDeliveryModeService()
	{
		return deliveryModeService;
	}

	@Required
	public void setDeliveryModeService(final DeliveryModeService deliveryModeService)
	{
		this.deliveryModeService = deliveryModeService;
	}

	protected UnitService getUnitService()
	{
		return unitService;
	}

	@Required
	public void setUnitService(final UnitService unitService)
	{
		this.unitService = unitService;
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}
}
