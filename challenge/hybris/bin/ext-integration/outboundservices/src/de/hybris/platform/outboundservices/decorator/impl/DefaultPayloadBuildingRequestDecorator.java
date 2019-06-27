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
package de.hybris.platform.outboundservices.decorator.impl;

import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class DefaultPayloadBuildingRequestDecorator implements OutboundRequestDecorator
{
	private IntegrationObjectConversionService integrationObjectConversionService;

	@Override
	public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload,
			final DecoratorContext context, final DecoratorExecution execution)
	{
		final Map<String, Object> map =
				getIntegrationObjectConversionService().convert(context.getItemModel(), context.getIntegrationObjectCode());

		payload.putAll(map);

		return execution.createHttpEntity(httpHeaders, payload, context);
	}

	protected IntegrationObjectConversionService getIntegrationObjectConversionService()
	{
		return integrationObjectConversionService;
	}

	@Required
	public void setIntegrationObjectConversionService(final IntegrationObjectConversionService integrationObjectConversionService)
	{
		this.integrationObjectConversionService = integrationObjectConversionService;
	}
}
