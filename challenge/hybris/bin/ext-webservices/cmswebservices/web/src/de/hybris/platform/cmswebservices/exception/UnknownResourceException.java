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
package de.hybris.platform.cmswebservices.exception;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

/**
 * UnkownResource validation exception used to throw validation errors. <br/>
 */

public class UnknownResourceException extends WebserviceException
{
	private static final long serialVersionUID = 691123443175113694L;

	private static final String TYPE = "UnknownResourceError";

	public UnknownResourceException(final String message)
	{
		super(message);
	}

	@Override
	public String getSubjectType()
	{
		return null;
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}