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
package de.hybris.platform.sap.productconfig.services.cache.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.services.cache.CacheKeyGenerator;
import de.hybris.platform.sap.productconfig.services.constants.SapproductconfigservicesConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class CacheKeyGeneratorImpl implements CacheKeyGenerator
{
	protected static final String NO_ACTIVE_TENANT = "NO_ACTIVE_TENANT";
	protected static final String KEY_CONFIG_ID = "CONFIG_ID";
	protected static final String KEY_PRODUCT_CODE = "PRODUCT_CODE";
	protected static final String KEY_USER_ID = "USER_ID";
	protected static final String TYPECODE_ANALYTICS_DATA = "__ANALYTICS_DATA__";
	protected static final String TYPECODE_PRICE_SUMMARY = "__PRICE_SUMMARY__";
	protected static final String TYPECODE_CONFIG = "__CONFIG__";
	protected static final String TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES = "__CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES__";

	private ProductConfigurationUserIdProvider userIdProvider;
	private SessionService sessionService;

	@Override
	public ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId)
	{
		return createCacheKey(configId, TYPECODE_PRICE_SUMMARY);
	}

	@Override
	public ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId)
	{
		return createCacheKey(configId, TYPECODE_ANALYTICS_DATA);
	}

	@Override
	public ProductConfigurationCacheKey createConfigCacheKey(final String configId)
	{
		return createCacheKey(configId, TYPECODE_CONFIG);
	}

	@Override
	public ProductConfigurationCacheKey createClassificationSystemCPQAttributesCacheKey(final String productCode)
	{
		return createCacheKeyWithoutUserKey(productCode, TYPECODE_CLASSIFICATION_SYSTEM_CPQ_ATTRIBUTES);
	}

	protected ProductConfigurationCacheKey createCacheKey(final String configId, final String typeCode)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_CONFIG_ID, configId);
		if (isSessionBoundToConfiguration() || !getUserIdProvider().isAnonymousUser())
		{
			keys.put(KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		}
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, typeCode, getTenantId());
	}

	protected ProductConfigurationCacheKey createCacheKeyWithoutUserKey(final String productCode, final String typeCode)
	{
		final Map<String, String> keys = new HashMap<>();
		keys.put(KEY_PRODUCT_CODE, productCode);
		return new ProductConfigurationCacheKey(keys, CacheUnitValueType.SERIALIZABLE, typeCode, getTenantId());
	}

	protected String getTenantId()
	{
		if (Registry.hasCurrentTenant())
		{
			return Registry.getCurrentTenant().getTenantID();
		}
		return NO_ACTIVE_TENANT;
	}

	protected ProductConfigurationUserIdProvider getUserIdProvider()
	{
		return userIdProvider;
	}

	@Required
	public void setUserIdProvider(final ProductConfigurationUserIdProvider userIdProvider)
	{
		this.userIdProvider = userIdProvider;
	}

	protected SessionService getSessionService()
	{
		return this.sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected boolean isSessionBoundToConfiguration()
	{
		boolean sessionBoundToConfiguration = true;
		final Object attribute = getSessionService()
				.getAttribute(SapproductconfigservicesConstants.SESSION_NOT_BOUND_TO_CONFIGURATIONS);
		if (attribute instanceof Boolean)
		{
			sessionBoundToConfiguration = !((Boolean) attribute).booleanValue();
		}
		return sessionBoundToConfiguration;
	}

}
