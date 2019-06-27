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

package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

public class PersistenceErrorRuntimeException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.BAD_REQUEST;
	private static final String RUNTIME_EXCEPTION_MESSAGE = "There was an error encountered during the processing of " +
			"the integration object. The detailed cause of this error is visible in the log.";

	public PersistenceErrorRuntimeException(final Throwable throwable, final String integrationKey)
	{
		super(RUNTIME_EXCEPTION_MESSAGE, STATUS_CODE, "runtime_error", throwable, integrationKey);
	}
}
