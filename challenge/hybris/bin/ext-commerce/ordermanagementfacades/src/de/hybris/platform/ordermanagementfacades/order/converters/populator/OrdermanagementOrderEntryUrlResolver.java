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

import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.core.model.order.OrderEntryModel;

import org.springframework.beans.factory.annotation.Required;


/**
 * Resolves the URI for the given {@link OrderEntryModel}, using the {@value OrderEntryModel#ORDER}
 */
public class OrdermanagementOrderEntryUrlResolver extends AbstractUrlResolver<OrderEntryModel>
{
	private final String CACHE_KEY = OrdermanagementOrderEntryUrlResolver.class.getName();
	private String pattern;

	@Override
	protected String getKey(final OrderEntryModel source)
	{
		return CACHE_KEY + "." + source.getPk().toString();
	}

	@Override
	protected String resolveInternal(final OrderEntryModel source)
	{
		String url = getPattern();

		if (url.contains("{code}"))
		{
			url = url.replace("{code}", urlEncode(source.getOrder().getCode()).replaceAll("\\+", "%20"));
		}
		if (url.contains("{entryNumber}"))
		{
			url = url.replace("{entryNumber}", urlEncode(source.getEntryNumber().toString()).replaceAll("\\+", "%20"));
		}
		return url;
	}

	protected String getPattern()
	{
		return pattern;
	}

	@Required
	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}

}
