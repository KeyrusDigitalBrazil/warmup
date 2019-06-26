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
package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Checked exception for errors in the configuration runtime
 */
public class ConfigurationEngineException extends Exception
{
	/**
	 * default constructor
	 */
	public ConfigurationEngineException()
	{
		super();
	}


	/**
	 * Constructor with message and cause
	 * 
	 * @param message
	 *           message of the exception
	 * @param cause
	 *           cause of the exception
	 */
	public ConfigurationEngineException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
