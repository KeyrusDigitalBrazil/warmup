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
package de.hybris.platform.sap.core.bol.businessobject;


/**
 * Exceptions which is thrown if the backend determination of the business object fails.
 */
public class BackendDeterminationRuntimeException extends BORuntimeException
{

	private static final long serialVersionUID = 5299225184712667677L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendDeterminationRuntimeException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public BackendDeterminationRuntimeException()
	{
		super();
	}

	/**
	 * Standard constructor for BackendDeterminationRuntimeException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public BackendDeterminationRuntimeException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}

}
