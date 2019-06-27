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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.KnowledgeBaseHeadersCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;


/**
 * Default implementation of {@link CharonKbDeterminationFacade}
 */
public class CharonKbDeterminationFacadeImpl implements CharonKbDeterminationFacade
{

	private ObjectMapper objectMapper;
	private RequestErrorHandler requestErrorHandler;
	private KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService;
	private CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;

	private static final ThreadLocal<DateFormat> kbDateFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

	@Override
	public Integer readKbIdForDate(final String productcode, final Date kbDate)
	{
		List<CPSMasterDataKBHeaderInfo> knowledgebases = getAllKBsOfProduct(productcode);
		knowledgebases = filterKBsByDate(kbDate, knowledgebases);
		if (CollectionUtils.isEmpty(knowledgebases))
		{
			throw new IllegalStateException("No KB found for product and date: " + productcode + " / " + kbDate);
		}
		return knowledgebases.get(0).getId();
	}

	@Override
	public boolean hasKbForDate(final String productcode, final Date kbDate)
	{
		List<CPSMasterDataKBHeaderInfo> knowledgebases = getAllKBsOfProduct(productcode);
		knowledgebases = filterKBsByDate(kbDate, knowledgebases);
		return CollectionUtils.isNotEmpty(knowledgebases);
	}

	@Override
	public boolean hasKBForKey(final KBKey kbKey)
	{
		final List<CPSMasterDataKBHeaderInfo> knowledgebases = getAllKBsOfProduct(kbKey.getProductCode());
		return isKBkeyInList(knowledgebases, kbKey);
	}

	@Override
	public boolean hasValidKBForKey(final KBKey kbKey)
	{
		List<CPSMasterDataKBHeaderInfo> knowledgebases = getAllKBsOfProduct(kbKey.getProductCode());
		knowledgebases = filterKBsByDate(kbKey.getDate(), knowledgebases);
		return isKBkeyInList(knowledgebases, kbKey);
	}

	@Override
	public KBKey parseKBKeyFromExtConfig(final String productCode, final String externalcfg)
	{
		CPSCommerceExternalConfiguration externalConfigStructured;
		try
		{
			externalConfigStructured = getObjectMapper().readValue(externalcfg, CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing from JSON failed", e);
		}
		final CPSExternalConfiguration cpsFormat = getCommerceExternalConfigurationStrategy()
				.extractCPSFormatFromCommerceRepresentation(externalConfigStructured);
		final CPSMasterDataKnowledgebaseKey cpsKBKey = cpsFormat.getKbKey();
		final KBKey kbKey = new KBKeyImpl(productCode, cpsKBKey.getName(), cpsKBKey.getLogsys(), cpsKBKey.getVersion());
		return kbKey;
	}


	protected List<CPSMasterDataKBHeaderInfo> getAllKBsOfProduct(final String productcode)
	{
		List<CPSMasterDataKBHeaderInfo> knowledgebases = Collections.emptyList();
		try
		{
			knowledgebases = getKnowledgeBasesCacheAccessService().getKnowledgeBases(productcode);
		}
		catch (final HttpException ex)
		{
			getRequestErrorHandler().processHasKbError(ex);
		}
		return knowledgebases;
	}

	protected final List<CPSMasterDataKBHeaderInfo> filterKBsByDate(final Date kbDate,
			final List<CPSMasterDataKBHeaderInfo> knowledgebases)
	{
		return knowledgebases.stream().filter(kb -> isKBValidOnDate(kbDate, kb)).collect(Collectors.toList());
	}

	protected boolean isKBValidOnDate(final Date kbDate, final CPSMasterDataKBHeaderInfo kb)
	{
		try
		{
			final Date kbValidFrom = kbDateFormat.get().parse(kb.getValidFromDate());
			return kbValidFrom.before(kbDate);
		}
		catch (final ParseException ex)
		{
			throw new IllegalStateException("Could not parse KB valid from date: " + kb.getValidFromDate(), ex);
		}
	}

	protected boolean isKBkeyInList(final List<CPSMasterDataKBHeaderInfo> knowledgebases, final KBKey kbKey)
	{
		return knowledgebases.stream().anyMatch(kb -> isKBHeaderMatchingKBKey(kb, kbKey));
	}

	protected boolean isKBHeaderMatchingKBKey(final CPSMasterDataKBHeaderInfo kb, final KBKey kbKey)
	{
		final CPSMasterDataKnowledgebaseKey cpsKbKey = kb.getKey();
		return kbKey.getKbLogsys().equals(cpsKbKey.getLogsys()) && kbKey.getKbName().equals(cpsKbKey.getName())
				&& kbKey.getKbVersion().equals(cpsKbKey.getVersion());
	}


	/**
	 * @return the commerceExternalConfigurationStrategy
	 */
	protected CommerceExternalConfigurationStrategy getCommerceExternalConfigurationStrategy()
	{
		return commerceExternalConfigurationStrategy;
	}

	/**
	 * @return the objectMapper
	 */
	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}

	/**
	 * Set the error handler for REST service calls
	 *
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}

	protected KnowledgeBaseHeadersCacheAccessService getKnowledgeBasesCacheAccessService()
	{
		return knowledgeBasesCacheAccessService;
	}

	/**
	 * Set the service to access the kb cache
	 *
	 * @param knowledgeBasesCacheAccessService
	 *           the knowledgeBasesCacheAccessService to set
	 */
	@Required
	public void setKnowledgeBasesCacheAccessService(final KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService)
	{
		this.knowledgeBasesCacheAccessService = knowledgeBasesCacheAccessService;
	}

	/**
	 * Set the configuration strategy for external configurations
	 *
	 * @param commerceExternalConfigurationStrategy
	 *           The commerceExternalConfigurationStrategy to set
	 */
	public void setCommerceExternalConfigurationStrategy(
			final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy)
	{
		this.commerceExternalConfigurationStrategy = commerceExternalConfigurationStrategy;

	}

}
