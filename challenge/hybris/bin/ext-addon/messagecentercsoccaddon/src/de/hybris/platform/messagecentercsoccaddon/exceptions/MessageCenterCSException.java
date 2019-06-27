/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.messagecentercsoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class MessageCenterCSException extends WebserviceException
{

	private static final String TYPE = "MessageCenterCSError";
	private static final String SUBJECT_TYPE = "MessageCenterCS";
	private final String errorCode;

	public MessageCenterCSException(final String errorCode, final String message)
	{
		super(message);
		this.errorCode = errorCode;
	}


	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

}