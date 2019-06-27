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

import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;


public class InvalidIntegrationKeyException extends InvalidDataException
{
	public InvalidIntegrationKeyException(final String integrationKey, final String entityType)
	{
		super("invalid_key", String.format(
				"The integration key [%s] is invalid. Please consult the IntegrationKey definition of [%s] for configuration details.",
				integrationKey, entityType));
	}

	public InvalidIntegrationKeyException(final String entityType)
	{
		super("invalid_key", String.format(
				"The integration key is invalid. Please consult the IntegrationKey definition of [%s] for configuration details.",
				entityType));
	}
}
