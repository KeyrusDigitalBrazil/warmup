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
package de.hybris.platform.selectivecartfacades.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.selectivecartservices.enums.CartSourceType;


/**
 * Populates {@link AbstractOrderEntryModel} to {@link OrderEntryData}
 */
public class SelectiveCartOrderEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData>
{
	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		if (source.getAddToCartTime() == null)
		{
			target.setAddToCartTime(source.getCreationtime());
		}
		else
		{
			target.setAddToCartTime(source.getAddToCartTime());
		}
		target.setCartSourceType(CartSourceType.STOREFRONT);
	}
}
