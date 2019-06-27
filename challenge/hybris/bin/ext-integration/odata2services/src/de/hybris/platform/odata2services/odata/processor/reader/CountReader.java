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
package de.hybris.platform.odata2services.odata.processor.reader;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.springframework.beans.factory.annotation.Required;

/**
 * The {@code CountReader} reads number of items in the platform matching the request conditions.
 */
public class CountReader extends AbstractEntityReader
{
	private ModelEntityService entityService;

	@Override
	public boolean isApplicable(final UriInfo uriInfo)
	{
		return uriInfo.isCount();
	}

	/**
	 * {@inheritDoc}
	 * <p>This method reads only number of items matching the conditions specified in the {@code request}.</p>
	 *
	 * @param request parameter object that contains the request with the conditions for the items to be counted.
	 * @return a response containing a number of matching items in its body. The response content type is {@code plain/text} for
	 * that reason
	 * @throws ODataException when the request violates the OData protocol
	 */
	@Override
	public ODataResponse read(final ItemLookupRequest request) throws ODataException
	{
		final int count = entityService.count(request);
		return ODataResponse.newBuilder()
				.entity(count)
				.header("Content-Type", "text/plain")
				.status(HttpStatusCodes.OK)
				.build();
	}

	protected ModelEntityService getEntityService()
	{
		return entityService;
	}

	@Required
	public void setEntityService(final ModelEntityService service)
	{
		entityService = service;
	}
}
