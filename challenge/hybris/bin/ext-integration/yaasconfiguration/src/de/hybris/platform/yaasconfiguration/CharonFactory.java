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
package de.hybris.platform.yaasconfiguration;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.STRING_CONSTANT_DOT;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CACHE_DELIMITER;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.hybris.charon.Charon;
import com.hybris.charon.CharonBuilder;


public class CharonFactory
{
	private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

	public <T> T client(final String appId, final Class<T> clientType, final Map<String, String> yaasConfig,
			final Function<CharonBuilder<T>, T> builder)
	{
		checkArgument(appId != null, "appId must not be null");
		checkArgument(clientType != null, "clientType must not be null");
		checkArgument(yaasConfig != null && !yaasConfig.isEmpty(), "yaasConfig must not be empty");
		checkArgument(builder != null, "builder must not be null");

		// cache key consists of Yaas appId + YAAS_CACHE_DELIMITER + clientType
		// it might have different client for the same yaas appid : example : yaasApp1#productClient , yaasApp1#category
		return (T) cache.computeIfAbsent(buildCacheKey(appId, clientType.getName()),
				k -> builder.apply(Charon.from(clientType).config(yaasConfig)));

	}

	/**
	 * Invalidate the cache for the given key
	 *
	 * @param key
	 */
	public void inValidateCache(final String key)
	{
		checkArgument(key != null, "key must not be null");

		// Remove the cache if it exactly matches the key
		if (cache.containsKey(key))
		{
			cache.remove(key);
		}
		// If the caller notify to invalidate all the client corresponding to the
		// given yaas appid , then it should invalidate all the client associated with
		// the given yass appid.
		// Cache might have different client for the same yaas appid : example : yaasApp1#productClient , yaasApp1#category
		else
		{
			for (final String keyValue : cache.keySet())
			{
				if (StringUtils.indexOf(keyValue, key + YAAS_CACHE_DELIMITER) >= 0)
				{
					cache.remove(keyValue);
				}
			}
		}
	}

	/**
	 * Helper method to build cache key which holds yaas client proxy.
	 *
	 * @param appId
	 * @param clientFileName
	 *
	 * @return yaas appId + # + yaas client name
	 */
	protected String buildCacheKey(final String appId, final String clientFileName)
	{
		return appId + YAAS_CACHE_DELIMITER + StringUtils.substringAfterLast(clientFileName, STRING_CONSTANT_DOT);
	}

	/**
	 * remove all clients from the cache
	 */
	public void clearCache()
	{
		cache.clear();
	}

}
