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

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.factory.ClientFactory;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.apiregistryservices.services.ClientCredentialPopulatingStrategy;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Default implementation of {@link ApiRegistryClientService}
 */
public class DefaultApiRegistryClientService implements ApiRegistryClientService
{
    public static final String CLIENT_URL = "url";
    public static final String OAUTH_URL = "oauth.url";
    public static final String OAUTH_CLIENT_ID = "oauth.clientId";
    public static final String OAUTH_CLIENT_SECRET = "oauth.clientSecret";
    public static final String TENANT = "tenant";
    public static final String CLIENT_SCOPE = "oauth.scope";

    private ClientFactory clientFactory;
    private DestinationService<AbstractDestinationModel> destinationService;
    private ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;
    private Map<Class, ClientCredentialPopulatingStrategy> credentialsStrategyMap;

    @Override
    public <T> T lookupClient(final Class<T> clientType) throws CredentialException
    {
        checkArgument(clientType != null, "clientType must not be null");

        final ConsumedDestinationModel destination = getConsumedDestinationLocatorStrategy().lookup(clientType.getSimpleName());

        if (destination == null)
        {
            throw new ModelNotFoundException(
                  "Failed to find consumed destination for the given id : {}" + clientType.getSimpleName());
        }

        final Map<String, String> clientConfig = buildClientConfig(clientType, destination);

        return getClientFactory().client(getClientFactory().buildCacheKey(destination), clientType, clientConfig);
    }

    @Override
    public Map<String, String> buildClientConfig(final Class clientType, final ConsumedDestinationModel destination)
          throws CredentialException
    {
        checkArgument(clientType != null, "clientType must not be null");

        final Map<String, String> config = new HashMap<>();
        buildDestinationConfig(config, destination);

        if (destination.getCredential() != null)
        {
            final ClientCredentialPopulatingStrategy populatingStrategy = getCredentialsStrategyMap()
                  .get(destination.getCredential().getClass());
            if (populatingStrategy != null)
            {
                populatingStrategy.populateConfig(destination.getCredential(), config);
            }
        }

        return config;
    }

    protected void buildDestinationConfig(final Map<String, String> config, final ConsumedDestinationModel destination)
    {
        checkArgument(destination != null, "destination must not be null");
        checkArgument(config != null, "config must not be null");

        config.put(CLIENT_URL, destination.getUrl());
        config.put(TENANT, destination.getDestinationTarget() != null ? destination.getDestinationTarget().getId() : null);

        if (null == destination.getAdditionalProperties().get(CLIENT_SCOPE))
        {
            config.put(CLIENT_SCOPE, StringUtils.EMPTY);
        }

        if (MapUtils.isNotEmpty(destination.getAdditionalProperties()))
        {
            final Map<String, String> filteredProperties = new HashMap<>(destination.getAdditionalProperties());
            filteredProperties.values().removeIf(StringUtils::isEmpty);
            config.putAll(filteredProperties);
        }
    }

    protected ClientFactory getClientFactory()
    {
        return clientFactory;
    }

    @Required
    public void setClientFactory(final ClientFactory clientFactory)
    {
        this.clientFactory = clientFactory;
    }

    protected DestinationService<AbstractDestinationModel> getDestinationService()
    {
        return destinationService;
    }

    @Required
    public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
    {
        this.destinationService = destinationService;
    }

    protected ConsumedDestinationLocatorStrategy getConsumedDestinationLocatorStrategy()
    {
        return consumedDestinationLocatorStrategy;
    }

    @Required
    public void setConsumedDestinationLocatorStrategy(
          ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy)
    {
        this.consumedDestinationLocatorStrategy = consumedDestinationLocatorStrategy;
    }

    protected Map<Class, ClientCredentialPopulatingStrategy> getCredentialsStrategyMap()
    {
        return credentialsStrategyMap;
    }

    @Required
    public void setCredentialsStrategyMap(Map<Class, ClientCredentialPopulatingStrategy> credentialsStrategyMap)
    {
        this.credentialsStrategyMap = credentialsStrategyMap;
    }

}
