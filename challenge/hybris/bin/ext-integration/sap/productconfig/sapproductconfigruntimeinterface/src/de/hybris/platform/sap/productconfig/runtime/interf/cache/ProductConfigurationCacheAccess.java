/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.cache;

import de.hybris.platform.regioncache.CacheValueLoader;

import java.util.Set;
import java.util.function.Supplier;


/**
 * The <code>ProductConfigurationCacheAccess</code> provides the direct access to a cache region.
 */
public interface ProductConfigurationCacheAccess<K, V>
{
	/**
	 * Gets a value from the cache for the given key. <br>
	 *
	 * @param key
	 *           K of object which should be retrieved from the cache
	 * @return the value or <code>null</code> if the value could not be found in the cache
	 */
	V get(K key);

	/**
	 * Provide the keys of all values, which are currently in the cache region.<br>
	 *
	 * @return all keys.
	 */
	Set<K> getKeys();

	/**
	 * Gets a value from the cache for the given key. If the value is not present in the cache the supplier will be called
	 * and the returned value will be cached.
	 *
	 * @param key
	 *           key of value
	 * @param supplier
	 *           supplier which returns the value
	 * @return the value
	 */
	V getWithSupplier(K key, Supplier<V> supplier);

	/**
	 * Puts a value into the cache region.
	 *
	 * @param key
	 *           key of value
	 * @param value
	 *           value which is put into the cache
	 */
	void put(K key, V value);

	/**
	 * Puts a value into the cache region, only if no value with same key already exists. <br>
	 *
	 * @param key
	 *           key of value
	 * @param value
	 *           value which is put into the cache
	 */
	void putIfAbsent(K key, V value);

	/**
	 * Removes a value from the cache. The cached value cannot be longer used.
	 *
	 * @param key
	 *           the key of the value which has to be removed from the cache
	 */
	void remove(K key);

	/**
	 * Gets a value from the cache using the loader to get the value. <br>
	 *
	 * @param key
	 *           the key of the value which has to loaded
	 * @param loader
	 *           the loader object which is used to load the value
	 * @return number of objects currently stored in the cache.
	 */
	V getWithLoader(K key, CacheValueLoader<V> loader);

	/**
	 * Removes all elements from cache.
	 */
	void clearCache();
}
