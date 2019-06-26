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
package com.sap.hybris.scpiconnector.exception;

/**
 *
 */
public class ScpiGenericException extends Exception
{

	private String code;
	private String reason;


	/**
	 * Constructor with errorCode and errorDescription
	 */
	public ScpiGenericException(final String code, final String reason)
	{
		super();
		this.code = code;
		this.reason = reason;
	}

	/**
	 * No argument constructor
	 */
	public ScpiGenericException()
	{
		super();
	}




	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return the reason
	 */
	public String getReason()
	{
		return reason;
	}

	@Override
	public String toString()
	{
		return String.format("SCPI generic exception. Error code [%s] and error reason [%s].", this.code, this.reason);
	}

}
