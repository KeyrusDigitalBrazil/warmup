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
 package de.hybris.platform.consignmenttrackingoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * consignment is not shipped exceptions
 */
public class NotShippedException extends WebserviceException
{


	public static final String NOT_SHIPPED = "notShipped";
	public static final String NOT_SHIPPED_MESSAGE = "The consignment is not shipped.";
	private static final String TYPE = "NotShippedError";
	private static final String SUBJECT_TYPE = "consignment";



	public NotShippedException(final String message)
	{
		super(message);
	}

	public NotShippedException(final String message, final String reason)
	{
		super(message, reason);
	}

	public NotShippedException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);

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


}
