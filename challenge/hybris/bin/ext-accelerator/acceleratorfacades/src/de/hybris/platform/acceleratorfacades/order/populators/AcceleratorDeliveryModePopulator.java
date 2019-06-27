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
package de.hybris.platform.acceleratorfacades.order.populators;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractDeliveryModePopulator;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;



public class AcceleratorDeliveryModePopulator extends AbstractDeliveryModePopulator<DeliveryModeModel, DeliveryModeData>
{

	@Override
	public void populate(final DeliveryModeModel source, final DeliveryModeData target)
	{
		super.populate(source, target);
	}

}
