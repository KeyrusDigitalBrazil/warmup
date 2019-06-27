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

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Exception for the {@link AssistedServiceFacade} which is used when incompatible customer id is provided for facade
 * method.
 */
public class AssistedServiceWrongCustomerIdException extends AssistedServiceException
{
	public AssistedServiceWrongCustomerIdException(final String message)
	{
		super(message);
	}

	public AssistedServiceWrongCustomerIdException(final String message, final UnknownIdentifierException e)
	{
		super(message, e);
	}

	@Override
	public String getMessageCode()
	{
		return "asm.emulate.error.customer";
	}

	@Override
	public String getAlertClass()
	{
		return ASM_ALERT_CUSTOMER;
	}
}
