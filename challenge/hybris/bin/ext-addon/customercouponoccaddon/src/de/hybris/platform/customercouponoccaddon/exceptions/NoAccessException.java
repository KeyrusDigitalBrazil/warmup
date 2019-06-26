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
package de.hybris.platform.customercouponoccaddon.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * NoAccess exception
 */
public class NoAccessException extends WebserviceException
{

	public static final String NO_ACCESS = "noAccess";

	private final String subjectType;

	public NoAccessException(final String message, final String reason, final String subject, final String subjectType)
	{
		super(message, reason, subject);
		this.subjectType = subjectType;
	}

	@Override
	public String getType()
	{
		return "NoAccessError";
	}

	@Override
	public String getSubjectType()
	{
		return subjectType;
	}

}
