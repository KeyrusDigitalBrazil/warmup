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

/**
 * A builder for {@link ResponseChangeSetEntity}
 */
public class ResponseChangeSetEntityBuilder
{
	private HttpStatus statusCode;
	private String integrationKey;
	private InboundRequestErrorModel requestError;

	public static ResponseChangeSetEntityBuilder responseChangeSetEntity()
	{
		return new ResponseChangeSetEntityBuilder();
	}

	public ResponseChangeSetEntityBuilder withStatusCode(final String code)
	{
		return withStatusCode(Integer.valueOf(code));
	}

	public ResponseChangeSetEntityBuilder withStatusCode(final int code)
	{
		statusCode = HttpStatus.valueOf(code);
		return this;
	}

	public ResponseChangeSetEntityBuilder withIntegrationKey(final String key)
	{
		integrationKey = key;
		return this;
	}

	public ResponseChangeSetEntityBuilder withRequestError(final InboundRequestErrorModel error)
	{
		requestError = error;
		return this;
	}

	public ResponseChangeSetEntity build()
	{
		return new ResponseChangeSetEntity(integrationKey, statusCode, requestError);
	}
}
