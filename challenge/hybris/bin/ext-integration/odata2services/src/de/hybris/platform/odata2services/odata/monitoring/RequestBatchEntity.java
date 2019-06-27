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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Information about a batch present in the request body.
 */
public class RequestBatchEntity
{
	private static final Logger LOG = LoggerFactory.getLogger(RequestBatchEntity.class);
	private static final byte[] NO_CONTENT = new byte[0];

	private final byte[] content;
	private final String messageId;
	private final String integrationObjectType;
	private final int numberOfChangeSets;

	/**
	 * Instantiates this {@code RequestBatchEntity}
	 * @param msgId message ID of the inbound request. It's common for all batches.
	 * @param payload the batch content from the request body
	 * @param objType integration object type. This is common for all batches.
	 * @param cnt number of change sets in this batch
	 */
	protected RequestBatchEntity(final String msgId, final InputStream payload, final String objType, final int cnt)
	{
		Preconditions.checkArgument(cnt > 0, "Number of change sets must be a positive number");
		messageId = Strings.nullToEmpty(msgId);
		integrationObjectType = Strings.nullToEmpty(objType);
		content = toByteArray(payload);
		numberOfChangeSets = cnt;
	}

	private byte[] toByteArray(final InputStream payload)
	{
		try
		{
			return payload != null ? IOUtils.toByteArray(payload) : NO_CONTENT;
		}
		catch (final IOException e)
		{
			LOG.warn("Failed to read payload for {} message with ID {} and integration key {}", integrationObjectType, messageId, e);
			return NO_CONTENT;
		}
	}

	public InputStream getContent()
	{
		return content == NO_CONTENT
				? null
				: new ByteArrayInputStream(content);
	}

	public String getMessageId()
	{
		return messageId;
	}

	public String getIntegrationObjectType()
	{
		return integrationObjectType;
	}

	public int getNumberOfChangeSets()
	{
		return numberOfChangeSets;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && this.getClass() == o.getClass())
		{
			final RequestBatchEntity entity = (RequestBatchEntity) o;

			assert content != null;
			assert messageId != null;
			assert integrationObjectType != null;
			return Arrays.equals(content, entity.content) &&
					messageId.equals(entity.messageId) &&
					integrationObjectType.equals(entity.integrationObjectType) &&
					numberOfChangeSets == entity.numberOfChangeSets;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(Arrays.hashCode(content), messageId, integrationObjectType, numberOfChangeSets);
	}

	@Override
	public String toString()
	{
		return "RequestBatchEntity{" +
				"messageId='" + messageId + '\'' +
				", integrationObjectType='" + integrationObjectType + '\'' +
				", numberOfChangeSets=" + numberOfChangeSets +
				'}';
	}
}
