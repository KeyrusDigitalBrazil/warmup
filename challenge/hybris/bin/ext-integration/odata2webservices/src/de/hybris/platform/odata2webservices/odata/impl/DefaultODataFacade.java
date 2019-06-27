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

package de.hybris.platform.odata2webservices.odata.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ODATA_REQUEST;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.odata2services.odata.EdmxProviderValidator;
import de.hybris.platform.odata2services.odata.ODataWebException;
import de.hybris.platform.odata2webservices.odata.DefaultIntegrationODataRequestHandler;
import de.hybris.platform.odata2webservices.odata.ODataFacade;

/**
 * A default implementation of the {@link ODataFacade}. This implementation delegates to the odata classes to derive the
 * ODataResponse for the request.
 */
public class DefaultODataFacade implements ODataFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultODataFacade.class);

	private ODataServiceFactory oDataServiceFactory;
	private EdmxProviderValidator edmxProviderValidator;

	@Override
	public ODataResponse handleGetSchema(final ODataContext oDataContext)
	{
		final ODataResponse oDataResponse = getResponse(oDataContext);
		return validatedResponse(oDataResponse);
	}

	@Override
	public ODataResponse handleGetEntity(final ODataContext oDataContext)
	{
		return getResponse(oDataContext);
	}

	@Override
	public ODataResponse handlePost(final ODataContext oDataContext)
	{
		return getResponse(oDataContext);
	}

	private ODataResponse getResponse(final ODataContext oDataContext)
	{
		final ODataService oDataService = getODataService(oDataContext);
		return createRequestHandler(oDataContext, oDataService).handle(odataRequest(oDataContext));
	}

	protected ODataRequest odataRequest(final ODataContext oDataContext)
	{
		return (ODataRequest) oDataContext.getParameter(ODATA_REQUEST);
	}

	private static ODataService getODataService(final ODataContext oDataContext)
	{
		try
		{
			return oDataContext.getService();
		}
		catch (final ODataException e)
		{
			LOGGER.error("Cannot get ODataService from ODataContext: {}", oDataContext, e);
			throw new ODataWebException("Error while trying to get ODataService from the ODataContext.", e);
		}
	}

	protected DefaultIntegrationODataRequestHandler createRequestHandler(final ODataContext oDataContext, final ODataService oDataService)
	{
		return DefaultIntegrationODataRequestHandler.createHandler(oDataServiceFactory, oDataService, oDataContext);
	}

	protected ODataResponse validatedResponse(final ODataResponse oDataResponse)
	{
		if (oDataResponse.getStatus().getStatusCode() < 300)
		{
			getEdmxProviderValidator().validateResponse(oDataResponse);
		}
		return oDataResponse;
	}

	@Required
	public void setODataServiceFactory(final ODataServiceFactory oDataServiceFactory)
	{
		this.oDataServiceFactory = oDataServiceFactory;
	}

	protected EdmxProviderValidator getEdmxProviderValidator()
	{
		return edmxProviderValidator;
	}

	@Required
	public void setEdmxProviderValidator(final EdmxProviderValidator edmxProviderValidator)
	{
		this.edmxProviderValidator = edmxProviderValidator;
	}
}
