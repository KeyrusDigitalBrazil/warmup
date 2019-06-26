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
package de.hybris.platform.odata2webservices.odata;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.ODataRequestHandler;

public class DefaultIntegrationODataRequestHandler extends ODataRequestHandler
{
	private DefaultIntegrationODataRequestHandler(final ODataServiceFactory factory, final ODataService service,
			final ODataContext context)
	{
		super(factory, service, context);
	}

	public static DefaultIntegrationODataRequestHandler createHandler(final ODataServiceFactory factory, final ODataService service, final ODataContext context)
	{
		return new DefaultIntegrationODataRequestHandler(factory, service, context);
	}

	@Override
	public ODataResponse handle(final ODataRequest request)
	{
		final ODataResponse oDataResponse = super.handle(request);
		return getIntegrationResponse(oDataResponse);
	}

	protected ODataResponse getIntegrationResponse(final ODataResponse response)
	{
		return new IntegrationODataResponse().customBuilder().fromResponse(response).entity(response.getEntity()).build();
	}
}
