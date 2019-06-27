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
package de.hybris.platform.cmsfacades.exception;

/**
 * Exception thrown when attempting to add or move a component to a slot that doesn't support it.
 */
public class ComponentNotFoundInSlotException extends RuntimeException
{

	private static final long serialVersionUID = 7427385832156186056L;

	public ComponentNotFoundInSlotException(final String message)
    {
        super(message);
    }

    public ComponentNotFoundInSlotException(final Throwable cause)
    {
        super(cause);
    }

    public ComponentNotFoundInSlotException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}