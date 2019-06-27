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
package de.hybris.platform.sap.productconfig.runtime.interf.cache.impl;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ProductConfigurationEHCacheAccessImpl<K extends CacheKey, V> implements ProductConfigurationCacheAccess<K, V>
{
	private static final Logger LOGGER = Logger.getLogger(ProductConfigurationEHCacheAccessImpl.class);

	private EHCacheRegion cache;

	@Override
	public V get(final K key)
	{
		return (V) getCache().get(key);
	}

	@Override
	public Set<K> getKeys()
	{
		return new HashSet<K>((Collection<K>) getCache().getAllKeys());
	}

	@Override
	public V getWithSupplier(final K key, final Supplier<V> supplier)
	{
		return getWithLoader(key, new SupplierCacheValueLoaderImpl<>(supplier));
	}

	@Override
	public V getWithLoader(final K key, final CacheValueLoader<V> loader)
	{
		return (V) getCache().getWithLoader(key, loader);
	}

	@Override
	public void put(final K key, final V value)
	{
		if (getCache().containsKey(key))
		{
			remove(key);
		}

		final DefaultCacheValueLoaderImpl defaultLoader = new DefaultCacheValueLoaderImpl<>(value);
		getWithLoader(key, defaultLoader);
	}

	@Override
	public void putIfAbsent(final K key, final V value)
	{
		if (!getCache().containsKey(key))
		{
			put(key, value);
		}
	}

	@Override
	public void remove(final K key)
	{
		getCache().remove(key, false);
	}

	@Override
	public void clearCache()
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Cache region " + LOGGER.getName() + "cleared.");
		}
		getCache().clearCache();
	}

	protected EHCacheRegion getCache()
	{
		return cache;
	}

	@Required
	public void setCache(final EHCacheRegion cache)
	{
		this.cache = cache;
	}

	private static class DefaultCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{
		private final V value;

		public DefaultCacheValueLoaderImpl(final V value)
		{
			this.value = value;
		}

		@Override
		public V load(final CacheKey key)
		{
			return value;
		}

	}

	private static class SupplierCacheValueLoaderImpl<V> implements CacheValueLoader<V>
	{
		private final Supplier<V> supplier;

		public SupplierCacheValueLoaderImpl(final Supplier<V> supplier)
		{
			this.supplier = supplier;
		}

		@Override
		public V load(final CacheKey key)
		{
			return supplier.get();
		}

	}

}
