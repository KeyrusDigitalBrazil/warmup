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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 */
public abstract class AbstractOrderConfigurationPopulator
{
	/**
	 * Finds an entry part of the cart
	 *
	 * @param target
	 *           AbstractOrder DTO representation
	 * @param entryNumber
	 *           Number of item we search for
	 * @return Target order entry DTO
	 */
	protected OrderEntryData findTargetEntry(final AbstractOrderData target, final Integer entryNumber)
	{
		for (final OrderEntryData targetEntry : target.getEntries())
		{
			if (targetEntry.getEntryNumber().equals(entryNumber))
			{
				return targetEntry;
			}
		}
		return null;
	}

	/**
	 * Writes result to target entry DTO
	 *
	 * @param target
	 *           AbstractOrder DTO, used to get the cart entry DTO via searching for key
	 * @param entry
	 *           AbstractOrder entry model
	 */
	protected void writeToTargetEntry(final AbstractOrderEntryModel entry, final AbstractOrderData target)
	{
		final OrderEntryData targetEntry = findTargetEntry(target, entry.getEntryNumber());
		writeToTargetEntry(entry, targetEntry);
	}

	protected void writeToTargetEntry(final AbstractOrderEntryModel entry, final OrderEntryData targetEntry)
	{
		validateAndSetPK(entry, targetEntry);
		targetEntry.setConfigurationAttached(true);
		writeSummaryMap(entry, targetEntry);

		targetEntry.setConfigurationInfos(createConfigurationInfos(entry));
	}



	protected void validateAndSetPK(final AbstractOrderEntryModel entry, final OrderEntryData targetEntry)
	{
		if (targetEntry == null)
		{
			throw new IllegalArgumentException("Target items do not match source items");
		}
		targetEntry.setItemPK(entry.getPk().toString());
	}

	protected void writeSummaryMap(final AbstractOrderEntryModel entry, final OrderEntryData targetEntry)
	{
		final Map<ProductInfoStatus, Integer> cpqSummary = entry.getCpqStatusSummaryMap();
		if (cpqSummary == null)
		{
			final StringBuilder sb = new StringBuilder().append("OrderEntry with Pk ").append(entry.getPk().toString())
					.append(" has invalid cpqSummaryMap (null)");
			throw new IllegalStateException(sb.toString());
		}
		if (cpqSummary.isEmpty())
		{
			targetEntry.setConfigurationConsistent(true);
			targetEntry.setConfigurationErrorCount(0);
		}
		else
		{
			//cpqStatusSummaryMap is only filled when the configuration is inconsistent
			targetEntry.setConfigurationConsistent(false);
			targetEntry.setConfigurationErrorCount(cpqSummary.get(ProductInfoStatus.ERROR).intValue());
		}
		targetEntry.setStatusSummaryMap(cpqSummary);
	}


	protected List<ConfigurationInfoData> createConfigurationInfos(final AbstractOrderEntryModel entry)
	{
		final List<ConfigurationInfoData> configInfoData = new ArrayList<>();
		for (final AbstractOrderEntryProductInfoModel productInfo : entry.getProductInfos())
		{
			if (!(productInfo instanceof CPQOrderEntryProductInfoModel))
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("ProductInfo of entry ").append(entry.getPk())
						.append(" do not have the correct type CPQOrderEntryProductInfoModel");
				throw new ConversionException(sb.toString());
			}
			final CPQOrderEntryProductInfoModel source = (CPQOrderEntryProductInfoModel) productInfo;
			final ConfigurationInfoData targetData = new ConfigurationInfoData();
			targetData.setConfiguratorType(source.getConfiguratorType());
			targetData.setConfigurationLabel(source.getCpqCharacteristicName());
			targetData.setConfigurationValue(source.getCpqCharacteristicAssignedValues());
			targetData.setStatus(source.getProductInfoStatus());

			configInfoData.add(targetData);
		}
		return configInfoData;
	}
}
