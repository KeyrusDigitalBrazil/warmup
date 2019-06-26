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

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

/**
 * Decorates the outbound client request. By modifying headers and payload along the decorator chain.
 */
public interface OutboundRequestDecorator
{
	/**
	 * Decorates an Outbound request
	 * @param httpHeaders The headers to be used for the outgoing request.
	 * @param payload The payload used for the request
	 * @param decoratorContext Some extra information that can be used by decorators.
	 * @param execution The execution chain.
	 * @return An {@link HttpEntity} containing the result of the decoration. Normally by calling {@code execution.createHttpEntity()}
	 */
	HttpEntity<Map<String, Object>> decorate(HttpHeaders httpHeaders, Map<String, Object> payload,
											 DecoratorContext decoratorContext, DecoratorExecution execution);
}
