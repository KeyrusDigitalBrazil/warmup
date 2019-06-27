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

import de.hybris.platform.odata2services.odata.persistence.InvalidEntryDataException;

import org.apache.commons.lang3.StringUtils;


public class ODataPayloadProcessingException extends InvalidEntryDataException
{
	private static final String ODATA_PROCESSING_EXCEPTION_MESSAGE = "An unexpected error occurred while processing the "
			+ "request. The most likely cause of this error is the formatting of your OData request payload. The detailed "
			+ "cause of this error is visible in the log. %s";

	public ODataPayloadProcessingException(final Throwable t)
	{
		super("odata_error", String.format(ODATA_PROCESSING_EXCEPTION_MESSAGE, t.getMessage()), t, StringUtils.EMPTY);
	}
}
