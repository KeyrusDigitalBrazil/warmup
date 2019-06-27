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
package com.sap.platform.factory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.sap.platform.constants.SapcpconfigurationConstants.CACHE_DELIMITER;
import static com.sap.platform.constants.SapcpconfigurationConstants.STRING_CONSTANT_DOT;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.hybris.charon.Charon;
import com.hybris.charon.CharonBuilder;

public class SapCharonFactory {
	private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

	public <T> T client(final String tenant, final Class<T> clientType, final Map<String, String> CECConfig,
			final Function<CharonBuilder<T>, T> builder) {
		checkArgument(tenant != null, "Tenand Name must not be null");
		checkArgument(clientType != null, "clientType must not be null");
		checkArgument(CECConfig != null && !CECConfig.isEmpty(), "CECConfig must not be empty");
		checkArgument(builder != null, "builder must not be null");

		// cache key consists of CEC tenant + CEC_CACHE_DELIMITER + clientType
		// it might have different client for the same CEC appid : example :
		// CECApp1#productClient , CECApp1#category
		return (T) cache.computeIfAbsent(buildCacheKey(tenant, clientType.getName()),
				k -> builder.apply(Charon.from(clientType).config(CECConfig)));

	}

	/**
	 * Invalidate the cache for the given key
	 *
	 * @param key
	 */
	public void inValidateCache(final String key) {
		checkArgument(key != null, "key must not be null");

		// Remove the cache if it exactly matches the key
		if (cache.containsKey(key)) {
			cache.remove(key);
		}
		// If the caller notify to invalidate all the client corresponding to the
		// given CEC appid , then it should invalidate all the client associated with
		// the given CEC appid.
		// Cache might have different client for the same CEC appid : example :
		// CECApp1#productClient , CECApp1#category
		else {
			for (final String keyValue : cache.keySet()) {
				if (StringUtils.indexOf(keyValue, key + CACHE_DELIMITER) >= 0) {
					cache.remove(keyValue);
				}
			}
		}
	}

	/**
	 * Helper method to build cache key 
	 *
	 * @param tenant
	 * @param clientFileName
	 *
	 * @return CEC tenant + # + CEC client name
	 */
	protected String buildCacheKey(final String tenant, final String clientFileName) {
		return tenant + CACHE_DELIMITER + StringUtils.substringAfterLast(clientFileName, STRING_CONSTANT_DOT);
	}

	/**
	 * remove all clients from the cache
	 */
	public void clearCache() {
		cache.clear();
	}

}
