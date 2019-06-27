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
package de.hybris.platform.warehousingfacades.pointofservice.converters.populator;

import de.hybris.platform.commercefacades.storelocator.converters.populator.PointOfServicePopulator;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;


public class WarehousingPointOfServicePopulator extends PointOfServicePopulator
{
	private List<String> warehouseCodes;

	@Required
	public void setWarehouseCodes(final List<String> warehouseCodes)
	{
		this.warehouseCodes = warehouseCodes;
	}

	protected List<String> getWarehouseCodes()
	{
		return warehouseCodes;
	}

	@Override
	public void populate(final PointOfServiceModel source, final PointOfServiceData target)
	{
		super.populate(source, target);
		warehouseCodes = new ArrayList<>();
		source.getWarehouses().forEach(warehouse -> getWarehouseCodes().add(warehouse.getCode()));
		target.setWarehouseCodes(getWarehouseCodes());
	}

}
