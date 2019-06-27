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
package de.hybris.platform.apiregistryservices.services.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.OAUTH_URL;
import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.OAUTH_CLIENT_ID;
import static de.hybris.platform.apiregistryservices.services.impl.DefaultApiRegistryClientService.OAUTH_CLIENT_SECRET;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.ClientCredentialPopulatingStrategy;


/**
 * OAuth implementation of @{@link ClientCredentialPopulatingStrategy}
 */
public class OAuthClientCredentialPopulatingStrategy implements ClientCredentialPopulatingStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuthClientCredentialPopulatingStrategy.class);

	/**
	 * @see ClientCredentialPopulatingStrategy#populateConfig(AbstractCredentialModel, Map)
	 * @param credential
	 * @param config
	 *           config to be updated
	 * @throws CredentialException
	 */
	@Override
	public void populateConfig(AbstractCredentialModel credential, Map<String, String> config) throws CredentialException
	{
		checkArgument(config != null, "config must not be null");

		if (!(credential instanceof ConsumedOAuthCredentialModel))
		{
			final String errorMessage = "Missing Consumed OAuth Credential";
			LOG.error(errorMessage);
			throw new CredentialException(errorMessage);
		}
		final ConsumedOAuthCredentialModel oAuthCredential = (ConsumedOAuthCredentialModel) credential;

		config.put(OAUTH_URL, oAuthCredential.getOAuthUrl());
		config.put(OAUTH_CLIENT_ID, oAuthCredential.getClientId());
		config.put(OAUTH_CLIENT_SECRET, oAuthCredential.getClientSecret());
	}
}
