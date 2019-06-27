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
package de.hybris.platform.warehousingfacades.warehouse.converters.populator;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Warehousing Converter for converting {@link WarehouseModel}
 */
public class WarehousingWarehousePopulator implements Populator<WarehouseModel, WarehouseData>
{
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private Converter<ConsignmentModel, ConsignmentData> consignmentConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private Converter<WarehouseModel, WarehouseData> warehouseUrlConverter;

	@Override
	public void populate(final WarehouseModel source, final WarehouseData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setCode(source.getCode());
			target.setIsDefault(source.getDefault());
			target.setPriority(source.getPriority());

			target.setPointsOfServices(checkNotNullAndEmpty(source.getPointsOfService()) ? null :
					Converters.convertAll(source.getPointsOfService(), getPointOfServiceConverter()));
			target.setConsignments(checkNotNullAndEmpty(source.getConsignments()) ?
					null :
					Converters.convertAll(source.getConsignments(), getConsignmentConverter()));
			target.setDeliveryModes(checkNotNullAndEmpty(source.getDeliveryModes()) ?
					null :
					Converters.convertAll(source.getDeliveryModes(), getDeliveryModeConverter()));
			target.setUrl(getWarehouseUrlConverter().convert(source).getUrl());
			target.setExternal(source.isExternal());
		}
	}

	protected Boolean checkNotNullAndEmpty (final Collection collection)
	{
		return collection == null || collection.isEmpty();
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	@Required
	public void setConsignmentConverter(final Converter<ConsignmentModel, ConsignmentData> consignmentConverter)
	{
		this.consignmentConverter = consignmentConverter;
	}

	protected Converter<ConsignmentModel, ConsignmentData> getConsignmentConverter()
	{
		return consignmentConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	protected Converter<WarehouseModel, WarehouseData> getWarehouseUrlConverter()
	{
		return warehouseUrlConverter;
	}

	@Required
	public void setWarehouseUrlConverter(final Converter<WarehouseModel, WarehouseData> warehouseUrlConverter)
	{
		this.warehouseUrlConverter = warehouseUrlConverter;
	}

}
