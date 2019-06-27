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

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME;

import de.hybris.platform.integrationservices.service.SapPassportService;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class DefaultSapPassportRequestDecorator implements OutboundRequestDecorator
{
	private SapPassportService sapPassportService;

	@Override
	public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload,
													final DecoratorContext context, final DecoratorExecution execution)
	{
		final String sapPassport = getSapPassportService().generate(context.getIntegrationObjectCode());
		httpHeaders.add(SAP_PASSPORT_HEADER_NAME, sapPassport);

		return execution.createHttpEntity(httpHeaders, payload, context);
	}

	protected SapPassportService getSapPassportService()
	{
		return sapPassportService;
	}

	@Required
	public void setSapPassportService(final SapPassportService sapPassportService)
	{
		this.sapPassportService = sapPassportService;
	}
}
