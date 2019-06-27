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
package de.hybris.platform.apiregistryservices.factory.impl;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.apiregistryservices.factory.ClientFactory;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.hybris.charon.Charon;
import com.hybris.charon.CharonBuilder;


/**
 * The CharonFactory generates charon client instances
 */
public class CharonFactory implements ClientFactory
{
	private static final String CACHE_DELIMITER = "#";
	private static final String STRING_CONSTANT_DOT = ".";

	private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

	public <T> T client(final String cacheKey, final Class<T> clientType, final Map<String, String> clientConfig)
	{
		checkArgument(cacheKey != null, "credentialId must not be null");
		checkArgument(clientType != null, "clientType must not be null");
		checkArgument(clientConfig != null && !clientConfig.isEmpty(), "charonConfig must not be empty");

		final Function<CharonBuilder<T>, T> builder = CharonBuilder::build;

		return (T) cache.computeIfAbsent(cacheKey,
				k -> builder.apply(Charon.from(clientType).config(clientConfig)));
	}

	/**
	 * Invalidate the cache for the given key
	 *
	 * @param key
	 *           the key of client
	 */
	public void inValidateCache(final String key)
	{
		checkArgument(key != null, "key must not be null");

		if (cache.containsKey(key))
		{
			cache.remove(key);
		}
		else
		{
			for (final String keyValue : cache.keySet())
			{
				if (StringUtils.indexOf(keyValue, key + CACHE_DELIMITER) >= 0)
				{
					cache.remove(keyValue);
				}
			}
		}
	}

	/**
	 * Helper method to build cache key which holds client proxy.
	 *
	 * @param credentialId
	 *           the id of credential
	 * @param clientFileName
	 *           name of the client
	 * @return cache key string
	 * @deprecated since 1811.1. Please use buildCacheKey method of {@link ClientFactory}
	 */
	@Deprecated
	protected String buildCacheKey(final String credentialId, final String clientFileName)
	{
		return credentialId + CACHE_DELIMITER + StringUtils.substringAfterLast(clientFileName, STRING_CONSTANT_DOT);
	}

	public String buildCacheKey(final ConsumedDestinationModel destination)
	{
		checkArgument(destination != null, "Destination must not be null.");

		String cacheKey = destination.getId();

		if (Objects.nonNull(destination.getCredential()))
		{
			cacheKey = destination.getCredential().getId() + CACHE_DELIMITER + cacheKey;
		}

		return cacheKey;
	}

	/**
	 * Remove all clients from the cache
	 */
	public void clearCache()
	{
		cache.clear();
	}

	protected ConcurrentHashMap<String, Object> getCache()
	{
		return cache;
	}

}
