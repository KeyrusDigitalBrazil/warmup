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

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;

public class InvalidEntryDataException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.BAD_REQUEST;

	/**
	 * Constructor to create InvalidDataException
	 *
	 * @param errorCode error code
	 * @param e exception to get Message from
	 * @param integrationKey key value for the item
	 */
	public InvalidEntryDataException(final String errorCode, final String message, final Throwable e, final String integrationKey)
	{
		super(message, STATUS_CODE, errorCode, e, integrationKey);
	}
}
