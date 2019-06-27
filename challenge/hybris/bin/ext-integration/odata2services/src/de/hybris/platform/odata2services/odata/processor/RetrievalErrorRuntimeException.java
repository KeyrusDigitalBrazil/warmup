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

import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;

public class RetrievalErrorRuntimeException extends InvalidDataException
{
	private static final String RETRIEVAL_ITEM_RUNTIME_EXCEPTION_MESSAGE =
			"There was a problem with the retrieval of the requested [%s].";

	public RetrievalErrorRuntimeException(final String entityTypeName, final Throwable throwable)
	{
		super("runtime_error", String.format(RETRIEVAL_ITEM_RUNTIME_EXCEPTION_MESSAGE, entityTypeName), throwable);
	}
}
