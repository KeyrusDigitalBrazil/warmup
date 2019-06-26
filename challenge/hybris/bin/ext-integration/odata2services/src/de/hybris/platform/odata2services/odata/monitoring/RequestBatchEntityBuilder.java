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

package de.hybris.platform.odata2services.odata.monitoring;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.INTEGRATION_MESSAGE_ID;
import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.SERVICE;

import java.io.InputStream;

import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * A builder for {@link RequestBatchEntity}.
 */
public class RequestBatchEntityBuilder
{
	private InputStream batchContent;
	private String messageId;
	private String integrationObjectType;
	private int numberOfChangeSets = 1;

	public static RequestBatchEntityBuilder requestBatchEntity()
	{
		return new RequestBatchEntityBuilder();
	}

	public RequestBatchEntityBuilder withContext(final ODataContext context)
	{
		return withMessageId(context.getRequestHeader(INTEGRATION_MESSAGE_ID))
				.withIntegrationObjectType(deriveIntegrationObjectType(context));
	}

	public RequestBatchEntityBuilder withBatchContent(final InputStream body)
	{
		batchContent = body;
		return this;
	}
	
	public RequestBatchEntityBuilder withMessageId(final String id)
	{
		messageId = id;
		return this;
	}

	public RequestBatchEntityBuilder withIntegrationObjectType(final String type)
	{
		integrationObjectType = type;
		return this;
	}

	public RequestBatchEntityBuilder withNumberOfChangeSets(final int cnt)
	{
		numberOfChangeSets = cnt;
		return this;
	}

	public RequestBatchEntity build()
	{
		return new RequestBatchEntity(messageId, batchContent, integrationObjectType, numberOfChangeSets);
	}

	protected String deriveIntegrationObjectType(final ODataContext context)
	{
		final Object value = context.getParameter(SERVICE);
		return value instanceof String ? (String) value : null;
	}
}
