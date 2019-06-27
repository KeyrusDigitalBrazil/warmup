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
package de.hybris.platform.acceleratorcms.component.cache;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.regioncache.key.CacheKey;

import javax.servlet.http.HttpServletRequest;


/**
 * Defines an API to interact with CMS cache
 */
public interface CmsCacheService
{
	/**
	 * Checks if cache is enabled
	 *
	 * @param request
	 *           the current request
	 * @param component
	 *           the current component
	 * @return <tt>true</tt> if cache is enabled
	 */
	boolean useCache(HttpServletRequest request, AbstractCMSComponentModel component);

	/**
	 * Gets cache content by key
	 *
	 * @param key
	 *           the cache key
	 * @return the cache content
	 */
	String get(CacheKey key);

	/**
	 * Stores cache content by key
	 *
	 * @param key
	 *           the cache key
	 * @param content
	 *           the cache content
	 */
	void put(CacheKey key, String content);

	/**
	 * Gets the cache key for the current component and request
	 * 
	 * @param request
	 *           the current request
	 * @param component
	 *           the current component
	 * @return the cache key
	 */
	CacheKey getKey(HttpServletRequest request, AbstractCMSComponentModel component);

}
