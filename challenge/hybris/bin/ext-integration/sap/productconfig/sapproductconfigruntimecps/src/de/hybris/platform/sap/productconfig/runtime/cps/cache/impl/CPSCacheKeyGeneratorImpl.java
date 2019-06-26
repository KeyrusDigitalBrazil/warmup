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

import de.hybris.platform.core.Registry;
import de.hybris.platform.regioncache.key.CacheUnitValueType;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CPSCacheKeyGenerator}
 */
public class CPSCacheKeyGeneratorImpl implements CPSCacheKeyGenerator
{
	protected static final String CONFIGRATION_SERVICE_ID = ConfigurationClient.class.getSimpleName();
	protected static final String KB_DETERMINATION_SERVICE_ID = KbDeterminationClient.class.getSimpleName();
	protected static final String MASTER_DATA_SERVICE_ID = MasterDataClient.class.getSimpleName();
	protected static final String PRICING_SERVICE_ID = PricingClient.class.getSimpleName();
	protected static final String TYPECODE_COOKIE = "__COOKIE__";
	protected static final String TYPECODE_MASTER_DATA = "__MASTER_DATA__";
	protected static final String TYPECODE_KNOWLEDGEBASES = "__KNOWLEDGEBASES__";
	protected static final String TYPECODE_PRICING_DOCUMENT_DATA = "__PRICING_DOCUMENT_DATA__";
	protected static final String TYPECODE_VALUE_PRICES = "__VALUE_PRICES__";
	protected static final String TYPECODE_RUNTIME_CONFIGURATION = "__RUNTIME_CONFIGURATION__";
	private YaasConfigurationService yaasConfigurationService;
	private BaseSiteService baseSiteService;
	private ProductConfigurationUserIdProvider userIdProvider;
	/**
	 * Key for accessing the pricing document data type
	 */
	static final String PRICING_DOCUMENT_DATA_TYPE = "PRICING_DOCUMENT_DATA_TYPE";
	/**
	 * Key for accessing the language
	 */
	static final String KEY_PRODUCT = "PRODUCT";
	/**
	 * Key for accessing the language
	 */
	public static final String KEY_LANGUAGE = "LANGUAGE";
	/**
	 * Key for accessing the knowledgebase id
	 */
	public static final String KEY_KB_ID = "KB_ID";
	/**
	 * Key for accessing the user id
	 */
	static final String KEY_USER_ID = "USER_ID";
	/**
	 * Key for accessing the configuration id
	 */
	static final String KEY_CONFIG_ID = "CONFIG_ID";
	/**
	 * Key for accessing the cps service tenant
	 */
	static final String KEY_CPS_SERVICE_TENANT = "CPS_SERVICE_TENANT";
	/**
	 * Key for accessing the cps service url
	 */
	static final String KEY_CPS_SERVICE_URL = "CPS_SERVICE_URL";

	@Override
	public ProductConfigurationCacheKey createMasterDataCacheKey(final String kbId, final String lang)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(MASTER_DATA_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_KB_ID, kbId);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE, lang);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_MASTER_DATA, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createKnowledgeBaseHeadersCacheKey(final String product)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(KB_DETERMINATION_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_PRODUCT, product);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_KNOWLEDGEBASES, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createCookieCacheKey(final String configId)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(CONFIGRATION_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID, configId);
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_COOKIE, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createValuePricesCacheKey(final String kbId, final String pricingProduct)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(PRICING_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_KB_ID, kbId);
		if (null != pricingProduct)
		{
			keyMap.put(CPSCacheKeyGeneratorImpl.KEY_PRODUCT, pricingProduct);
		}
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.SERIALIZABLE, TYPECODE_VALUE_PRICES, getTenantId());
	}

	@Override
	public ProductConfigurationCacheKey createConfigurationCacheKey(final String configId)
	{
		final Map<String, String> keyMap = retrieveBasicCPSParameters(CONFIGRATION_SERVICE_ID);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CONFIG_ID, configId);
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_USER_ID, getUserIdProvider().getCurrentUserId());
		return new ProductConfigurationCacheKey(keyMap, CacheUnitValueType.NON_SERIALIZABLE, TYPECODE_RUNTIME_CONFIGURATION,
				getTenantId());
	}

	protected Map<String, String> retrieveBasicCPSParameters(final String serviceId)
	{
		final Pair<String, String> parameterPair = getCPSServiceParameter(serviceId);
		final Map<String, String> keyMap = new HashMap();
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_URL, parameterPair.getLeft());
		keyMap.put(CPSCacheKeyGeneratorImpl.KEY_CPS_SERVICE_TENANT, parameterPair.getRight());
		return keyMap;
	}

	protected String getTenantId()
	{
		return Registry.getCurrentTenant().getTenantID();
	}

	protected String getCurrentBaseSite()
	{
		return getBaseSiteService().getCurrentBaseSite().getUid();
	}

	protected Pair<String, String> getCPSServiceParameter(final String serviceId)
	{
		final YaasServiceModel serviceModel = getYaasConfigurationService().getYaasServiceForId(serviceId);

		final BaseSiteServiceMappingModel siteMapping = (BaseSiteServiceMappingModel) yaasConfigurationService
				.getBaseSiteServiceMappingForId(getCurrentBaseSite(), serviceModel);
		final YaasClientCredentialModel credentialModel = siteMapping.getYaasClientCredential();
		return Pair.of(serviceModel.getServiceURL(), credentialModel.getYaasProject().getIdentifier());
	}

	protected YaasConfigurationService getYaasConfigurationService()
	{
		return yaasConfigurationService;
	}

	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
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

}
