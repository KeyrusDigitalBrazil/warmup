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
package de.hybris.platform.odata2services.odata.impl;

import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;

import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * ODataErrorCallback implementation that populates the Context with
 * Status, ErrorCode and Message for custom OData runtime exceptions.
 */
public class CustomODataExceptionAwareErrorCallback implements ODataErrorCallback
{
	@Override
	public ODataResponse handleError(final ODataErrorContext context)
	{
		final Exception contextException = context.getException();
		ODataRuntimeApplicationException exception = null;
		if (contextException instanceof ODataRuntimeApplicationException)
		{
			exception = (ODataRuntimeApplicationException) contextException;
		}
		else if(contextException instanceof ODataException &&
				contextException.getCause() instanceof  ODataRuntimeApplicationException )
		{
			exception = (ODataRuntimeApplicationException) contextException.getCause();
		}

		if( exception != null )
		{
			context.setHttpStatus(exception.getHttpStatus());
			context.setErrorCode(exception.getCode());
			context.setLocale(exception.getLocale());
			context.setMessage(exception.getMessage());
			if(contextException instanceof PersistenceRuntimeApplicationException)
			{
				context.setInnerError(((PersistenceRuntimeApplicationException) contextException).getIntegrationKey());
			}
		}

		return EntityProvider.writeErrorDocument(context);
	}
}

