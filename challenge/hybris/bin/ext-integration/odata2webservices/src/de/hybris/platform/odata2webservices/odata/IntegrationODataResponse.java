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

import de.hybris.platform.odata2services.odata.RuntimeIOException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.ODataResponseImpl;

public class IntegrationODataResponse extends ODataResponseImpl
{
	private static final Logger LOG = Logger.getLogger(IntegrationODataResponse.class);

	private byte[] entityBuffer;

	@Override
	public InputStream getEntityAsStream() throws ODataException
	{
		if (entityBuffer == null || entityBuffer.length == 0)
		{
			bufferEntity(super.getEntityAsStream());
		}
		return new ByteArrayInputStream(this.entityBuffer);
	}

	public IntegrationODataResponse.IntegrationODataResponseBuilder customBuilder()
	{
		return new IntegrationODataResponse.IntegrationODataResponseBuilder();
	}

	protected void bufferEntity(final InputStream body)
	{
		try (InputStream localBody = body)
		{
			IntegrationODataResponse.this.entityBuffer = IOUtils.toByteArray(localBody);
		}
		catch (final IOException e)
		{
			LOG.error("There was a problem reading or closing the stream of the response entity", e);
			throw new RuntimeIOException(e);
		}
	}

	public class IntegrationODataResponseBuilder extends ODataResponseBuilderImpl
	{

		@Override
		public ODataResponseBuilder fromResponse(final ODataResponse response)
		{
			return super.fromResponse(response);
		}

		@Override
		public ODataResponse build()
		{
			super.build();

			return IntegrationODataResponse.this;
		}
	}
}
