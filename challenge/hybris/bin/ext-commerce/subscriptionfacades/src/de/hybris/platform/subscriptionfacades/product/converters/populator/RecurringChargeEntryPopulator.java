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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;

/**
 * Populate DTO {@link RecurringChargeEntryData} with data from {@link RecurringChargeEntryModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */
public class RecurringChargeEntryPopulator<SOURCE extends RecurringChargeEntryModel, TARGET extends RecurringChargeEntryData>
		extends AbstractChargeEntryPopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		target.setCycleStart(source.getCycleStart() == null ? 0 : source.getCycleStart());

		if (source.getCycleEnd() == null)
		{
			target.setCycleEnd(-1);
		}
		else
		{
			target.setCycleEnd(source.getCycleEnd());
		}

		super.populate(source, target);
	}
}
