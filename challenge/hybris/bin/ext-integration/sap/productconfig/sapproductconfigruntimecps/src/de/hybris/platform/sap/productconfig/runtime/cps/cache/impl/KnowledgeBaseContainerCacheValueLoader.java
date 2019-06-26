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

import de.hybris.platform.regioncache.CacheValueLoadException;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.impl.CPSTimer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;

import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Queries the CPS master data service to fill the CPS master data cache.
 */
public class KnowledgeBaseContainerCacheValueLoader implements CacheValueLoader<CPSMasterDataKnowledgeBaseContainer>
{
	private MasterDataClientBase clientSetExternally = null;
	private YaasServiceFactory yaasServiceFactory;
	private final CPSTimer timer = new CPSTimer();
	private final Scheduler scheduler = Schedulers.io();
	private static final Logger LOG = Logger.getLogger(KnowledgeBaseContainerCacheValueLoader.class);

	private Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> knowledgeBaseConverter;
	private ObjectMapper objectMapper;

	@Override
	public CPSMasterDataKnowledgeBaseContainer load(final CacheKey paramCacheKey)
	{
		if (!(paramCacheKey instanceof ProductConfigurationCacheKey))
		{
			throw new CacheValueLoadException("CacheKey is not instance of ProductConfigurationCacheKey");
		}
		final ProductConfigurationCacheKey key = (ProductConfigurationCacheKey) paramCacheKey;

		return getKnowledgeBaseConverter().convert(getKbFromService(key.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_KB_ID),
				key.getKeys().get(CPSCacheKeyGeneratorImpl.KEY_LANGUAGE)));
	}

	protected CPSMasterDataKnowledgeBase getKbFromService(final String kbId, final String lang)
	{
		try
		{
			LOG.info("Getting master data for KB with ID: " + kbId);
			timer.start("getKnowledgebase/" + kbId);
			final CPSMasterDataKnowledgeBase masterData = getClient()
					.getKnowledgebase(kbId, lang, SapproductconfigruntimecpsConstants.MASTER_DATA_ADDITIONAL_SELECTION)
					.subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (getKnowledgebase): ", masterData);
			}
			return masterData;
		}
		catch (final HttpException ex)
		{
			throw new CacheValueLoadException("Could not get knowledge base from service", ex);
		}
	}

	protected void traceJsonRequestBody(final String prefix, final Object obj)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(obj));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}


	protected MasterDataClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			return getYaasServiceFactory().lookupService(MasterDataClient.class);
		}
	}

	/**
	 * Set Charon client from outside (only used for testing)
	 *
	 * @param newClient
	 */
	public void setClient(final MasterDataClientBase newClient)
	{
		clientSetExternally = newClient;
	}

	protected Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> getKnowledgeBaseConverter()
	{
		return knowledgeBaseConverter;
	}

	/**
	 * @param knowledgeBaseConverter
	 *           the knowledgeBaseConverter to set
	 */
	@Required
	public void setKnowledgeBaseConverter(
			final Converter<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer> knowledgeBaseConverter)
	{
		this.knowledgeBaseConverter = knowledgeBaseConverter;
	}

	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	protected YaasServiceFactory getYaasServiceFactory()
	{
		return yaasServiceFactory;
	}

	/**
	 * @param yaasServiceFactory
	 *           the yaasServiceFactory to set
	 */
	@Required
	public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory)
	{
		this.yaasServiceFactory = yaasServiceFactory;
	}

}
