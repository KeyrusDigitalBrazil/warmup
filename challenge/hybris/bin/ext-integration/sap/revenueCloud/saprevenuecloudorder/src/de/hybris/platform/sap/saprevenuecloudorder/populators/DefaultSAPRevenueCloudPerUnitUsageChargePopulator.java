/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.ArrayList;
import java.util.List;

import com.sap.hybris.saprevenuecloudproduct.model.PerUnitUsageChargeEntryModel;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.product.converters.populator.PerUnitUsageChargePopulator;
import de.hybris.platform.subscriptionservices.model.PerUnitUsageChargeModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;

/**
 * Populate DTO {@link PerUnitUsageChargeData} with data from {@link PerUnitUsageChargeModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudPerUnitUsageChargePopulator extends PerUnitUsageChargePopulator<PerUnitUsageChargeModel,PerUnitUsageChargeData> {
	

	private Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> perUnitUsageChargeEntryConverter;

	@Override
	public void populate(final PerUnitUsageChargeModel source, final PerUnitUsageChargeData target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		
		super.populate(source, target);
		
		final List<UsageChargeEntryData> usageChargeEntries = new ArrayList<>();

		for (final UsageChargeEntryModel usageChargeEntry : source.getUsageChargeEntries())
		{
			if (usageChargeEntry instanceof PerUnitUsageChargeEntryModel)
			{
				usageChargeEntries.add(getPerUnitUsageChargeEntryConverter().convert((PerUnitUsageChargeEntryModel)usageChargeEntry));
			}
		}
		
		// Revisit this condition again TODO
		if(target.getUsageChargeEntries().isEmpty()) {
			target.setUsageChargeEntries(usageChargeEntries);
		}

		target.setBlockSize(source.getBlockSize());
		target.setIncludedQty(source.getIncludedQty());
		target.setMinBlocks(source.getMinBlocks());
	}


	public Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> getPerUnitUsageChargeEntryConverter()
	{
		return perUnitUsageChargeEntryConverter;
	}

	public void setPerUnitUsageChargeEntryConverter(
			Converter<PerUnitUsageChargeEntryModel, PerUnitUsageChargeEntryData> perUnitUsageChargeEntryConverter) 
	{
		this.perUnitUsageChargeEntryConverter = perUnitUsageChargeEntryConverter;
	}
}
