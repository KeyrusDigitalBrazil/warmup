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

import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;


/**
 * The <code>CommunicationException</code> is thrown if something goes wrong with the backend communication.
 */
public class CommunicationException extends BusinessObjectException
{



	private static final long serialVersionUID = 4824722828890426231L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param msgList
	 *           List of the messages added to the exception
	 */
	public CommunicationException(final String msg, final MessageList msgList)
	{
		super(msg, msgList);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param message
	 *           message added to the exception
	 */
	public CommunicationException(final String msg, final Message message)
	{
		super(msg, message);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public CommunicationException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor.
	 */
	public CommunicationException()
	{
		super();
	}

	/**
	 * Standard constructor for CommunicationException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CommunicationException(final String msg, final Throwable rootCause)
	{
		super(msg, rootCause);
	}


	/**
	 * Standard constructor for CommunicationException using a simple message text. <br>
	 * 
	 * @param msg
	 *           message text.
	 * @param message
	 *           {@link Message} object.
	 * @param rootCause
	 *           exception which causes the exception
	 */
	public CommunicationException(final String msg, final Message message, final Throwable rootCause)
	{
		super(msg, rootCause);
		this.messageList.add(message);
	}


}
