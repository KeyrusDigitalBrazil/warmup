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
package de.hybris.platform.sap.core.jco.rec.version000;

import de.hybris.platform.sap.core.jco.rec.JCoRecException;


/**
 * Exception for the JCoRecorder XML parser.
 */
@SuppressWarnings("serial")
public class JCoRecXMLParserException extends JCoRecException
{
	/**
	 * Calls {@code super()}.
	 */
	public JCoRecXMLParserException()
	{
		super();
	}

	/**
	 * Calls {@code super(message cause)}.
	 * 
	 * @param message
	 *           the message of the exception.
	 * @param cause
	 *           the cause of the exception.
	 */
	public JCoRecXMLParserException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Calls {@code super(message)}.
	 * 
	 * @param message
	 *           the message of the exception.
	 */
	public JCoRecXMLParserException(final String message)
	{
		super(message);
	}

	/**
	 * Calls {@code super(cause)}.
	 * 
	 * @param cause
	 *           the cause of the exception.
	 */
	public JCoRecXMLParserException(final Throwable cause)
	{
		super(cause);
	}
}
