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

public class MissingKeyPropertyException extends InvalidDataException
{
	public MissingKeyPropertyException(final String entityType, final String propertyName)
	{
		this(entityType, propertyName, null);
	}

	public MissingKeyPropertyException(final String entityType, final String propertyName, final Throwable cause)
	{
		super("missing_key", "Key [" + propertyName + "] is required for EntityType [" + entityType + "]", cause);
	}
}
