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

public class ODataContextProcessingException extends PersistenceRuntimeApplicationException
{
	private static final HttpStatusCodes STATUS_CODE = HttpStatusCodes.INTERNAL_SERVER_ERROR;
	private static final String ERROR_CODE = "odata_error";

	public ODataContextProcessingException(final Throwable cause, final String integrationKey)
	{
		super("ODataException thrown while extracting data from the ODataContext.", STATUS_CODE, ERROR_CODE, cause, integrationKey);
	}
}
