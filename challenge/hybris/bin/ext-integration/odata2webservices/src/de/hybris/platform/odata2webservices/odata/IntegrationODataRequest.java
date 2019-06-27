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
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.core.ODataRequestImpl;

public class IntegrationODataRequest extends ODataRequestImpl
{
	private static final Logger LOG = Logger.getLogger(IntegrationODataRequest.class);

	private byte[] bodyBuffer;


	@Override
	public InputStream getBody()
	{
		return new ByteArrayInputStream(this.bodyBuffer);
	}

	public IntegrationODataRequestBuilder customBuilder()
	{
		return new IntegrationODataRequestBuilder();
	}

	public class IntegrationODataRequestBuilder extends ODataRequestBuilderImpl
	{
		@Override
		public ODataRequest build()
		{
			super.build();

			return IntegrationODataRequest.this;
		}

		@Override
		public ODataRequestBuilder body(final InputStream body)
		{
			bufferBody(body);
			return super.body(new ByteArrayInputStream(bodyBuffer));
		}

		protected void bufferBody(final InputStream body)
		{
			try (InputStream localBody = body)
			{
				IntegrationODataRequest.this.bodyBuffer = IOUtils.toByteArray(localBody);
			}
			catch (final IOException e)
			{
				LOG.error("There was a problem reading or closing the stream of the request body");
				throw new RuntimeIOException(e);
			}
		}
	}
}
