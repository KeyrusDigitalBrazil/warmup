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
package de.hybris.platform.subscriptionfacades.product.converters.populator;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.TierUsageChargeEntryData;
import de.hybris.platform.subscriptionservices.model.TierUsageChargeEntryModel;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Populate DTO {@link TierUsageChargeEntryData} with data from {@link TierUsageChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */
public class TierUsageChargeEntryPopulator<SOURCE extends TierUsageChargeEntryModel, TARGET extends TierUsageChargeEntryData>
		extends AbstractUsageChargeEntryPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		target.setTierStart(source.getTierStart() == null ? 0 : source.getTierStart());
		target.setTierEnd(source.getTierEnd() == null ? 0 : source.getTierEnd());
		super.populate(source, target);
	}
}
