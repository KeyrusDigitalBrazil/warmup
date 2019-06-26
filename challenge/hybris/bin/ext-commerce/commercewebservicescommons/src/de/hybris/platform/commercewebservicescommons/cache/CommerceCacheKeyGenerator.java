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
package de.hybris.platform.commercewebservicescommons.cache;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.cache.CacheKeyGenerator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Cache key generator
 *
 * @spring.bean commerceCacheKeyGenerator
 *
 */
public class CommerceCacheKeyGenerator extends CacheKeyGenerator
{
	private BaseSiteService baseSiteService;


	/**
	 * Generates key based on given parameters and current session attributes ( base site, language, user, currency)
	 *
	 * @param addUserToKey
	 *           Define if current user uid should be added to key
	 * @param addCurrencyToKey
	 *           Define if current currency isocode should be added to key
	 * @param params
	 *           Values which should be added to key
	 * @return generated key
	 */
	@Override
	public Object generate(final boolean addUserToKey, final boolean addCurrencyToKey, final Object... params)
	{
		final List<Object> key = new ArrayList<>();
		addLanguage(key);
		addCurrentSite(key);
		addCurrency(addCurrencyToKey, key);
		addUser(addUserToKey, key);
		addParams(key, params);
		return key;
	}

	/**
	 * Generates key based on given parameters and current session attributes ( base site, language, user, currency).<br/>
	 * It uses Registry.getApplicationContext().getBean to have access to instance of commerceCacheKeyGenerator.<br/>
	 * This static method was added because @Cacheable annotation doesn't support using bean in SPeL expression for key :
	 * https://jira.spring.io/browse/SPR-9578
	 *
	 * @param addUserToKey
	 *           Define if current user uid should be added to key
	 * @param addCurrencyToKey
	 *           Define if current currency isocode should be added to key
	 * @param params
	 *           Values which should be added to key
	 * @return generated key
	 */
	public static Object generateKey(final boolean addUserToKey, final boolean addCurrencyToKey, final Object... params)
	{
		final CommerceCacheKeyGenerator keyGeneratorBean = (CommerceCacheKeyGenerator) Registry.getApplicationContext().getBean(
				"commerceCacheKeyGenerator");
		return keyGeneratorBean.generate(addUserToKey, addCurrencyToKey, params);
	}


	protected void addCurrentSite(final List<Object> key)
	{
		final BaseSiteModel currentSite = baseSiteService.getCurrentBaseSite();
		key.add(currentSite == null ? null : currentSite.getUid());
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}