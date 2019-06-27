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
package de.hybris.platform.integration.cis.subscription.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.OverageUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.TierUsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;

import com.hybris.cis.api.subscription.model.CisUsageChargeTier;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate the CisUsageChargeTierEntry with the UsageChargeEntryData information
 */
public class CisUsageChargeTierPopulator implements Populator<UsageChargeEntryData, CisUsageChargeTier>
{
	@Override
	public void populate(final UsageChargeEntryData source, final CisUsageChargeTier target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);

		if (source == null)
		{
			return;
		}

		target.setChargePrice(source.getPrice().getValue());

		if (source instanceof OverageUsageChargeEntryData)
		{
			target.setNumberOfUnits(Integer.valueOf(0));
		}
		else if (source instanceof TierUsageChargeEntryData)
		{
			final TierUsageChargeEntryData tierUsageChargeEntryData = (TierUsageChargeEntryData) source;
			target.setNumberOfUnits(Integer.valueOf(tierUsageChargeEntryData.getTierEnd() - tierUsageChargeEntryData.getTierStart()));
		}
	}
}
