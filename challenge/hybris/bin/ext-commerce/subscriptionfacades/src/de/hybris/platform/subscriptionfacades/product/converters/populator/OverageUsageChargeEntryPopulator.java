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
import de.hybris.platform.subscriptionfacades.data.OverageUsageChargeEntryData;
import de.hybris.platform.subscriptionservices.model.OverageUsageChargeEntryModel;


/**
 * Populate DTO {@link OverageUsageChargeEntryData} with data from {@link OverageUsageChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */
public class OverageUsageChargeEntryPopulator<SOURCE extends OverageUsageChargeEntryModel, TARGET extends OverageUsageChargeEntryData>
		extends AbstractUsageChargeEntryPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		super.populate(source, target);
	}
}
