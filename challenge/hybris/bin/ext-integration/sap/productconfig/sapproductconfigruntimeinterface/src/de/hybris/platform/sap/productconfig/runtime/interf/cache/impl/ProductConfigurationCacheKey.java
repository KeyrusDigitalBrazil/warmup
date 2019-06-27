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

import de.hybris.platform.regioncache.key.AbstractCacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

import java.util.Map;

import com.google.common.collect.ImmutableMap;


/**
 * Default implementation for a cache key in sap productconfig The field keys contains the additional cache keys. The
 * keys of the map correspond to the names of the additional fields. The values of the map correspond to the actual
 * value of the additional field.
 */
public class ProductConfigurationCacheKey extends AbstractCacheKey
{
	private final Map<String, String> keys;

	/**
	 * Default constructor
	 *
	 * @param keys
	 *           map of additional fields to the cache key
	 * @param valueType
	 *           indicates whether the caches valueType is serializable
	 * @param typeCode
	 *           type code
	 * @param tenantId
	 *           hybris tenant
	 */
	public ProductConfigurationCacheKey(final Map<String, String> keys, final CacheUnitValueType valueType, final String typeCode,
			final String tenantId)
	{
		super(valueType, typeCode, tenantId);
		this.keys = (keys == null) ? null : ImmutableMap.copyOf(keys);
	}

	/**
	 * Constructor which assumes that the cache's valueType is not serializable
	 *
	 * @param keys
	 *           map of additional fields to the cache key
	 * @param typeCode
	 *           type code
	 * @param tenantId
	 *           hybris tenant
	 */
	public ProductConfigurationCacheKey(final Map<String, String> keys, final String typeCode, final String tenantId)
	{
		super(typeCode, tenantId);
		this.keys = (keys == null) ? null : ImmutableMap.copyOf(keys);
	}

	@Override
	public int hashCode()
	{
		if (cachedHash == 0)
		{
			final int prime = 31;
			final int result = super.hashCode();
			this.cachedHash = prime * result + ((keys == null) ? 0 : keys.hashCode());
		}
		return this.cachedHash;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		final ProductConfigurationCacheKey other = (ProductConfigurationCacheKey) obj;
		if (keys == null)
		{
			if (other.keys != null)
			{
				return false;
			}
		}
		else if (!keys.equals(other.keys))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "ProductConfigurationCacheKey [keys=" + keys + ", valueType=" + valueType + ", tenantId=" + tenantId
				+ ", cachedHash=" + cachedHash + ", typeCode=" + typeCode + "]";
	}

	public Map<String, String> getKeys()
	{
		return keys;
	}


}
