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
package de.hybris.platform.customerinterestsfacades.productinterest.populators;


import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestEntryData;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.Date;
import java.util.Map.Entry;

import org.springframework.util.Assert;


public class ProductInterestEntryPopulator
		implements Populator<Entry<NotificationType, Date>, ProductInterestEntryData>
{
	@Override
	public void populate(final Entry<NotificationType, Date> source, final ProductInterestEntryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setInterestType(source.getKey().name());
		target.setDateAdded(source.getValue());
	}
}
