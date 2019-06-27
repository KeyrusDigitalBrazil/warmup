/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementfacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates the URI for the given {@link OrderEntryModel}
 */
public class OrdermanagementOrderEntryUrlPopulator implements Populator<OrderEntryModel, OrderEntryData>
{
	private UrlResolver<OrderEntryModel> orderEntryUrlResolver;

	@Override
	public void populate(final OrderEntryModel source, final OrderEntryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setUrl(getOrderEntryUrlResolver().resolve(source));
	}

	protected UrlResolver<OrderEntryModel> getOrderEntryUrlResolver()
	{
		return orderEntryUrlResolver;
	}

	@Required
	public void setOrderEntryUrlResolver(final UrlResolver<OrderEntryModel> orderEntryUrlResolver)
	{
		this.orderEntryUrlResolver = orderEntryUrlResolver;
	}

}
