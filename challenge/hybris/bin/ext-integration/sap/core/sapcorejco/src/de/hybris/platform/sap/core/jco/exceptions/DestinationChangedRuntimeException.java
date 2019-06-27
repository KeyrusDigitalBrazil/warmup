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
package de.hybris.platform.sap.core.jco.exceptions;

/**
 * Exception is thrown in case of administrator changes destination data in hmc/backoffice. <br>
 * Jco runtime invalidates all current used destinations. Subsequent access to an invalidated destination throws a
 * specific jco exception, which is transformed in a DestinationChangedRuntimeException.
 */
public class DestinationChangedRuntimeException extends BackendRuntimeException
{

	private static final long serialVersionUID = 8371796871878845045L;

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException()
	{
		super();
	}

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}

	@SuppressWarnings("javadoc")
	public DestinationChangedRuntimeException(final String msg)
	{
		super(msg);
	}

}
