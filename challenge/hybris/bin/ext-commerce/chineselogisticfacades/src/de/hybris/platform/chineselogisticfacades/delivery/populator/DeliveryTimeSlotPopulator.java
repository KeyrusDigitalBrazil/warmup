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
package de.hybris.platform.chineselogisticfacades.delivery.populator;

import de.hybris.platform.chineselogisticfacades.delivery.data.DeliveryTimeSlotData;
import de.hybris.platform.chineselogisticservices.model.DeliveryTimeSlotModel;
import de.hybris.platform.converters.Populator;


/**
 * Populating from DeliveryTimeSlotModel to DeliveryTimeSlotData
 */
public class DeliveryTimeSlotPopulator implements Populator<DeliveryTimeSlotModel, DeliveryTimeSlotData>
{

	@Override
	public void populate(final DeliveryTimeSlotModel source, final DeliveryTimeSlotData target)
	{
		target.setCode(source.getCode());
		target.setName(source.getName());
	}

}
