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
package de.hybris.platform.odata2services.odata.persistence.exception;

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;


/**
 * Exception to throw for error scenarios produced by invalid Data.
 * Will result in HttpStatus 404
 */
public class ItemNotFoundException extends ODataRuntimeApplicationException
{
	private static final String ITEM_NOT_FOUND_MESSAGE = "[%s] with integration key [%s] was not found.";

	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.NOT_FOUND;
	private static final String NOT_FOUND_CODE = "not_found";

	/**
	 * Constructor to create ItemNotFoundException
	 *
	 * @param entityType entity type
	 * @param integrationKey integration key
	 */
	public ItemNotFoundException(final String entityType, final String integrationKey)
	{
		super(String.format(ITEM_NOT_FOUND_MESSAGE, entityType, integrationKey), Locale.ENGLISH, STATUS_CODE, NOT_FOUND_CODE);
	}

	/**
	 * Constructor to create ItemNotFoundException
	 *
	 * @param entityType entity type
	 * @param integrationKey integration key
	 * @param e exception to get Message from
	 */
	public ItemNotFoundException(final String entityType, final String integrationKey, final Throwable e)
	{
		super(String.format(ITEM_NOT_FOUND_MESSAGE, entityType, integrationKey), Locale.ENGLISH, STATUS_CODE, NOT_FOUND_CODE, e);
	}
}
