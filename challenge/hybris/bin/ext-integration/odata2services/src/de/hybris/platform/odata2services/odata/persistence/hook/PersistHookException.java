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
package de.hybris.platform.odata2services.odata.persistence.hook;

import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

public abstract class PersistHookException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.BAD_REQUEST;

	public PersistHookException(final String message, final String errorCode, final Throwable cause, final String integrationKey)
	{
		super(message, STATUS_CODE, errorCode, cause, integrationKey);
	}

	public PersistHookException(final String message, final String errorCode, final String integrationKey)
	{
		super(message, STATUS_CODE, errorCode, integrationKey);
	}
}
