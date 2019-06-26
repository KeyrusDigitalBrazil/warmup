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
package de.hybris.platform.sap.sapcarintegration.exceptions;

public class CarConnectionRuntimeException extends RuntimeException
{

	private static final long serialVersionUID = -6810533990715272730L;

	public CarConnectionRuntimeException(String msg, Exception e)
	{
		super(msg, e);
	}
	
	public CarConnectionRuntimeException(String msg)
	{
		super(msg);
	}
}
