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
package de.hybris.platform.sap.core.jco.monitor;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseException;


/**
 * Exception which occurs during JCo monitoring.
 * 
 */
public class JCoMonitorException extends CoreBaseException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1344124319839369351L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public JCoMonitorException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public JCoMonitorException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
