/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.POST_PERSIST_HOOK;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.PRE_PERSIST_HOOK;
import static de.hybris.platform.odata2services.odata.persistence.StorageRequest.storageRequestBuilder;

import de.hybris.platform.odata2services.odata.processor.ServiceNameExtractor;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultStorageRequestFactory implements StorageRequestFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultStorageRequestFactory.class);

	private ServiceNameExtractor serviceNameExtractor;
	private ODataContextLanguageExtractor localeExtractor;
	private ModelEntityService modelEntityService;

	@Override
	public StorageRequest create(final ODataContext oDataContext, final String responseContentType, final EdmEntitySet entitySet, final ODataEntry entry)
	{
		final String integrationKey = getModelEntityService().addIntegrationKeyToODataEntry(entitySet, entry);

		final Locale contentLocale = getLocaleExtractor().extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE);
		final Locale acceptLocale = getAcceptLocale(oDataContext, contentLocale);

		try
		{
			return storageRequestBuilder()
					.withIntegrationKey(integrationKey)
					.withEntitySet(entitySet)
					.withODataEntry(entry)
					.withAcceptLocale(acceptLocale)
					.withContentLocale(contentLocale)
					.withPrePersistHook(oDataContext.getRequestHeader(PRE_PERSIST_HOOK))
					.withPostPersistHook(oDataContext.getRequestHeader(POST_PERSIST_HOOK))
					.withIntegrationObject(getServiceNameFromContext(oDataContext, integrationKey))
					.withServiceRoot(oDataContext.getPathInfo().getServiceRoot())
					.withContentType(responseContentType)
					.withRequestUri(oDataContext.getPathInfo().getRequestUri())
					.build();
		}
		catch (final ODataException e)
		{
			LOG.error("Exception while extracting path info from the ODataContext for Item with integrationKey {}", integrationKey);
			throw new ODataContextProcessingException(e, integrationKey);
		}
	}

	private Locale getAcceptLocale(final ODataContext oDataContext, final Locale contentLocale)
	{
		return getLocaleExtractor().getAcceptLanguage(oDataContext)
				.orElse(contentLocale);
	}

	private String getServiceNameFromContext(final ODataContext oDataContext, final String integrationKey)
	{
		return getServiceNameExtractor().extract(oDataContext, integrationKey);
	}

	protected ServiceNameExtractor getServiceNameExtractor()
	{
		return serviceNameExtractor;
	}

	@Required
	public void setServiceNameExtractor(final ServiceNameExtractor serviceNameExtractor)
	{
		this.serviceNameExtractor = serviceNameExtractor;
	}

	protected ODataContextLanguageExtractor getLocaleExtractor()
	{
		return localeExtractor;
	}

	@Required
	public void setLocaleExtractor(final ODataContextLanguageExtractor localeExtractor)
	{
		this.localeExtractor = localeExtractor;
	}

	protected ModelEntityService getModelEntityService()
	{
		return modelEntityService;
	}

	@Required
	public void setModelEntityService(final ModelEntityService modelEntityService)
	{
		this.modelEntityService = modelEntityService;
	}
}
