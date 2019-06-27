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


import java.net.URI;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Request for a CRUD operation.
 */
public abstract class CrudRequest extends AbstractRequest
{
	private String integrationKey;
	private ODataEntry oDataEntry;
	private URI serviceRoot;
	private String contentType;
	private URI requestUri;

	public String getIntegrationKey()
	{
		return integrationKey;
	}

	protected void setIntegrationKey(final String integrationKey)
	{
		this.integrationKey = integrationKey;
	}

	public ODataEntry getODataEntry()
	{
		return oDataEntry;
	}

	protected void setODataEntry(final ODataEntry oDataEntry)
	{
		this.oDataEntry = oDataEntry;
	}

	public URI getServiceRoot()
	{
		return serviceRoot;
	}

	protected void setServiceRoot(final URI serviceRoot)
	{
		this.serviceRoot = serviceRoot;
	}

	public String getContentType()
	{
		return contentType;
	}

	protected void setContentType(final String contentType)
	{
		this.contentType = contentType;
	}

	public URI getRequestUri()
	{
		return requestUri;
	}

	protected void setRequestUri(final URI requestUri)
	{
		this.requestUri = requestUri;
	}

	public static class DataRequestBuilder<T extends CrudRequest.DataRequestBuilder, R extends CrudRequest> extends AbstractRequestBuilder<T, R>
	{
		protected DataRequestBuilder(final R request)
		{
			super(request);
		}

		public T withIntegrationKey(final String integrationKey)
		{
			request().setIntegrationKey(integrationKey);
			return myself();
		}

		public T withODataEntry(final ODataEntry oDataEntry)
		{
			request().setODataEntry(oDataEntry);
			return myself();
		}

		public T withIntegrationObject(final String code)
		{
			request().setIntegrationObjectCode(code);
			return myself();
		}

		public T withServiceRoot(final URI serviceRoot)
		{
			request().setServiceRoot(serviceRoot);
			return myself();
		}

		public T withContentType(final String contentType)
		{
			request().setContentType(contentType);
			return myself();
		}

		public T withRequestUri(final URI currentRequestUri)
		{
			request().setRequestUri(currentRequestUri);
			return myself();
		}

		@Override
		public T from(final R request)
		{
			withODataEntry(request.getODataEntry());
			return super.from(request);
		}
	}
}
