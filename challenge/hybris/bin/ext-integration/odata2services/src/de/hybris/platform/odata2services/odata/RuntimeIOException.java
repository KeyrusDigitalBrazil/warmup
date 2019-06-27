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
package de.hybris.platform.odata2services.odata;

import java.io.IOException;

/**
 * This exception wraps a thrown {@link IOException} as a runtime exception
 */
public class RuntimeIOException extends RuntimeException
{
	public RuntimeIOException(final IOException exception)
	{
		super("An error occurred while closing an IO stream", exception);
	}
}
