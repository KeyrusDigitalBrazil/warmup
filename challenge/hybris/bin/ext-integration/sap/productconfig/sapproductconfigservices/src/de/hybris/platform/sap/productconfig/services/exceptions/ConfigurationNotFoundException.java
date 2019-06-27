/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.exceptions;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

public class ConfigurationNotFoundException extends ModelNotFoundException
{
	public ConfigurationNotFoundException(final String message)
	{
		super(message);
	}

	public ConfigurationNotFoundException(final Throwable cause)
	{
		super(cause);
	}

	public ConfigurationNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
