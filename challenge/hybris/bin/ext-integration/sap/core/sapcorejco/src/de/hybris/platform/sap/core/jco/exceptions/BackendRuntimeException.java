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

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;


/**
 * Exception for Errors caused by the developer of the isales application. <br>
 * Don't catch this Exception except in the action layer because it represents an error in the implementation and not
 * the underlying backend system.
 */
public class BackendRuntimeException extends CoreBaseRuntimeException
{

	private static final long serialVersionUID = 7983522208154525237L;


	/**
	 * Creates a new exception without any message.
	 */
	public BackendRuntimeException()
	{
		super();
	}

	/**
	 * Creates a new exception with a given message.
	 * 
	 * @param msg
	 *           Message to give further information about the exception
	 */
	public BackendRuntimeException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public BackendRuntimeException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
