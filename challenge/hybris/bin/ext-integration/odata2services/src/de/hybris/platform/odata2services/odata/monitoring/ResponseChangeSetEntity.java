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

import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.integrationservices.util.HttpStatus;

import java.util.Optional;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * A DTO for representing a response changeset. One or more {@code ResponseChangeSetEntity} will be associated with one {@link RequestBatchEntity}
 */
public class ResponseChangeSetEntity
{
	private final HttpStatus statusCode;
	private final String integrationKey;
	private final InboundRequestErrorModel requestError;

	protected ResponseChangeSetEntity(final String key, final HttpStatus code, final InboundRequestErrorModel error)
	{
		integrationKey = Strings.nullToEmpty(key);
		statusCode = code;
		requestError = error;
	}

	protected HttpStatus getStatusCode()
	{
		return statusCode;
	}

	public String getIntegrationKey()
	{
		return integrationKey;
	}

	public Optional<InboundRequestErrorModel> getRequestError()
	{
		return Optional.ofNullable(requestError);
	}

	public boolean isSuccessful()
	{
		return statusCode != null && statusCode.isSuccessful();
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
			final ResponseChangeSetEntity entity = (ResponseChangeSetEntity) o;
			return statusCode == entity.statusCode &&
					integrationKey.equals(entity.integrationKey) &&
					Objects.equal(requestError, entity.requestError);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(statusCode, integrationKey, requestError);
	}

	@Override
	public String toString()
	{
		return "ResponseChangeSetEntity{" +
				"statusCode=" + statusCode +
				", integrationKey='" + integrationKey + '\'' +
				", requestError=" + requestError +
				'}';
	}
}
