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
package de.hybris.platform.warehousingfacades.order.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.warehousing.model.PackagingInfoModel;
import de.hybris.platform.warehousingfacades.order.data.PackagingInfoData;


/**
 * Warehousing Converter for converting a {@link PackagingInfoData}
 */
public class WarehousingReversePackagingInfoPopulator implements Populator<PackagingInfoData, PackagingInfoModel>
{
	@Override
	public void populate(final PackagingInfoData source, final PackagingInfoModel target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setWidth(source.getWidth());
			target.setHeight(source.getHeight());
			target.setLength(source.getLength());
			target.setGrossWeight(source.getGrossWeight());
			target.setInsuredValue(source.getInsuredValue());
			target.setDimensionUnit(source.getDimensionUnit());
			target.setWeightUnit(source.getWeightUnit());
		}
	}
}
