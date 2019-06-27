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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.WeekdayOpeningDayData;
import de.hybris.platform.storelocator.model.WeekdayOpeningDayModel;


public class WeekdayOpeningDayPopulator extends OpeningDayPopulator<WeekdayOpeningDayModel, WeekdayOpeningDayData>
{

	@Override
	public void populate(final WeekdayOpeningDayModel source, final WeekdayOpeningDayData target)
	{
		populateBase(source, target);
		target.setWeekDay(getWeekDaySymbols().get(source.getDayOfWeek().ordinal()));
	}
}
