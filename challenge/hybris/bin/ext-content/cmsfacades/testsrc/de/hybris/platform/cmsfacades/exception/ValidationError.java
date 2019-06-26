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

import org.springframework.validation.AbstractBindingResult;


/**
 * Error class to be used for testing purpose only; implements the {@link org.springframework.validation.Errors}
 * interface.
 */
public class ValidationError extends AbstractBindingResult
{
	private static final long serialVersionUID = 2687980944733761368L;

	public ValidationError(final String objectName)
	{
		super(objectName);
	}

	@Override
	public Object getTarget()
	{
		return null;
	}

	@Override
	protected Object getActualFieldValue(final String s)
	{
		return null;
	}

}
