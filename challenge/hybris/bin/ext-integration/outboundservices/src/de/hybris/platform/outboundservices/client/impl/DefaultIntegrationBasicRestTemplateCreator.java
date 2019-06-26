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
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * The default implementation for OAuth2RestTemplate creator.
 */
public class DefaultIntegrationBasicRestTemplateCreator extends AbstractRestTemplateCreator
{
	@Override
	public RestOperations create(final ConsumedDestinationModel destination)
	{
		if (isApplicable(destination))
		{
			final BasicCredentialModel credential = (BasicCredentialModel) destination.getCredential();

			final RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

			final BasicAuthorizationInterceptor basicAuthorizationInterceptor =
					new BasicAuthorizationInterceptor(credential.getUsername(), credential.getPassword());
			addInterceptors(restTemplate, basicAuthorizationInterceptor);
			addMessageConverters(restTemplate);

			return restTemplate;
		}

		throw new UnsupportedRestTemplateException();
	}

	@Override
	public boolean isApplicable(final ConsumedDestinationModel destination)
	{
		return destination.getCredential() instanceof BasicCredentialModel;
	}
}
