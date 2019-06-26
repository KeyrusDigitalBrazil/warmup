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
 */
package de.hybris.platform.outboundservices.decorator;

import java.util.Iterator;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class DefaultDecoratorExecution implements DecoratorExecution
{
	private Iterator<OutboundRequestDecorator> iterator;

	public DefaultDecoratorExecution(final Iterator<OutboundRequestDecorator> iterator)
	{
		this.iterator = iterator;
	}

	@Override
	public HttpEntity<Map<String, Object>> createHttpEntity(final HttpHeaders httpHeaders, final Map<String, Object> payload,
															final DecoratorContext context)
	{
		if( ! iterator.hasNext() )
		{
			return new HttpEntity<>(payload, httpHeaders);
		}

		final OutboundRequestDecorator decorator = iterator.next();
		return decorator.decorate(httpHeaders, payload, context, this);
	}
}
