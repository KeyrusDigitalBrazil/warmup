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

public class BatchLimitExceededException extends InvalidDataException
{
	private static final String BATCH_LIMIT_EXCEPTION_MESSAGE = "The number of integration objects sent in the " +
			"request has exceeded the 'odata2services.batch.limit' setting currently set to %s";

	public BatchLimitExceededException(final int limit)
	{
		super("batch_limit_exceeded", String.format(BATCH_LIMIT_EXCEPTION_MESSAGE, limit));
	}
}
