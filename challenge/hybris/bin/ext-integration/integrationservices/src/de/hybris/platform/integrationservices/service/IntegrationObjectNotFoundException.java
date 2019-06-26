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
package de.hybris.platform.integrationservices.service;

public class IntegrationObjectNotFoundException extends RuntimeException
{
	private static final String MSG = "The Integration Object definition for [%s] does not exist";

	private final String integrationObjectCode;

	public IntegrationObjectNotFoundException(final String integrationObjectCode)
	{
		super(String.format(MSG, integrationObjectCode));
		this.integrationObjectCode = integrationObjectCode;
	}

	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}
}
