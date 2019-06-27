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
package de.hybris.platform.subscriptionfacades.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

/**
 * Populator implementation for {@link BillingTimeModel} as source and {@link BillingTimeData} as target type.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */
public class BillingTimePopulator<SOURCE extends BillingTimeModel, TARGET extends BillingTimeData> implements
		Populator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		target.setCode(source.getCode());
		target.setName(source.getNameInCart());
		target.setNameInOrder(source.getNameInOrder());
		target.setDescription(source.getDescription());
		target.setOrderNumber(source.getOrder() == null ? 0 : source.getOrder());
	}
}
