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
package de.hybris.platform.odata2services.filter;

/**
 * Throws this exception when applying the filter caused the query to return no results
 */
public class NoFilterResultException extends RuntimeException
{
	public NoFilterResultException()
	{
		super("Applying the filter has no query result");
	}
}
