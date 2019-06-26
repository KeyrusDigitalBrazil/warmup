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
package de.hybris.platform.warehousingfacades.warehouse.impl;


import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingfacades.warehouse.WarehousingWarehouseFacade;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link WarehousingWarehouseFacade}
 */
public class DefaultWarehousingWarehouseFacade extends OmsBaseFacade implements WarehousingWarehouseFacade
{
	private Converter<WarehouseModel, WarehouseData> warehouseConverter;
	private WarehouseService warehouseService;

	@Override
	public WarehouseData getWarehouseForCode(final String code)
	{
		return getWarehouseConverter().convert(getWarehouseService().getWarehouseForCode(code));
	}


	protected Converter<WarehouseModel, WarehouseData> getWarehouseConverter()
	{
		return warehouseConverter;
	}

	@Required
	public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter)
	{
		this.warehouseConverter = warehouseConverter;
	}

	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	@Required
	public void setWarehouseService(WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

}
