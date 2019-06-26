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
package de.hybris.platform.odata2services.odata.persistence;

public class InvalidQueryParameterException extends InvalidDataException
{
	private static final String ERROR_CODE = "invalid_query_parameter";
	
	public InvalidQueryParameterException(final String message)
	{
		super(ERROR_CODE, message);
	}
}
