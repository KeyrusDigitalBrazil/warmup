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
package de.hybris.platform.assistedserviceservices.exception;


/**
 * Exception for the {@link AssistedServiceFacade} which is used when AS agent login has been blocked.
 */
public class AssistedServiceAgentBlockedException extends AssistedServiceException
{

	public AssistedServiceAgentBlockedException(final String message)
	{
		super(message);
	}

	@Override
	public String getMessageCode()
	{
		return "asm.login.blocked";
	}

	@Override
	public String getAlertClass()
	{
		return ASM_ALERT_CREDENTIALS;
	}
}