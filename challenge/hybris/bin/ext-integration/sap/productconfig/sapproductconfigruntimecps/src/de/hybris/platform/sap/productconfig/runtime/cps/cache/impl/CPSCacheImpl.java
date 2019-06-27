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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CPSCache}. Stores related artifacts in the hybris cache.
 */
public class CPSCacheImpl implements CPSCache
{

	private CPSCacheKeyGenerator keyGenerator;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<String>> cookieCache;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> valuePricesCache;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSConfiguration> configurationCache;

	@Override
	public void setValuePricesMap(final String kbId, final String pricingProduct,
			final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap)
	{
		getValuePricesCache().put(getKeyGenerator().createValuePricesCacheKey(kbId, pricingProduct), valuePricesMap);
	}

	@Override
	public Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getValuePricesMap(final String kbId, final String pricingProduct)
	{
		return getValuePricesCache().get(getKeyGenerator().createValuePricesCacheKey(kbId, pricingProduct));
	}



	@Override
	public void setCookies(final String configId, final List<String> cookieList)
	{
		getCookieCache().put(getKeyGenerator().createCookieCacheKey(configId), cookieList);
	}

	@Override
	public List<String> getCookies(final String configId)
	{
		return getCookieCache().get(getKeyGenerator().createCookieCacheKey(configId));
	}

	@Override
	public void removeCookies(final String configId)
	{
		getCookieCache().remove(getKeyGenerator().createCookieCacheKey(configId));
	}

	@Override
	public CPSConfiguration getConfiguration(final String configId)
	{
		return getConfigurationCache().get(getKeyGenerator().createConfigurationCacheKey(configId));
	}

	@Override
	public void setConfiguration(final String configId, final CPSConfiguration configuration)
	{
		getConfigurationCache().put(getKeyGenerator().createConfigurationCacheKey(configId), configuration);

	}

	@Override
	public void removeConfiguration(final String configId)
	{
		getConfigurationCache().remove(getKeyGenerator().createConfigurationCacheKey(configId));
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<String>> getCookieCache()
	{
		return cookieCache;
	}

	@Required
	public void setCookieCache(final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<String>> cookieCache)
	{
		this.cookieCache = cookieCache;
	}

	protected CPSCacheKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final CPSCacheKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> getValuePricesCache()
	{
		return valuePricesCache;
	}

	@Required
	public void setValuePricesCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> valuePricesCache)
	{
		this.valuePricesCache = valuePricesCache;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSConfiguration> getConfigurationCache()
	{
		return configurationCache;
	}

	@Required
	public void setConfigurationCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSConfiguration> configurationCache)
	{
		this.configurationCache = configurationCache;
	}


}
