/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.cis.client.shared.exception.codes;


/**
 * A service exception detail consists of a standard exception code and an additional message.
 * 
 *
 */
public class ServiceExceptionDetail
{

	private static final int ARBITRARY_HASH_31 = 31;
	private static final int ARBITRARY_HASH_7 = 7;
	private final String message;
	private final StandardServiceExceptionCode code;

	/**
	 * Instantiates a new {@link ServiceExceptionDetail}.
	 * 
	 * @param code a standard error code specified per module type
	 */
	public ServiceExceptionDetail(final StandardServiceExceptionCode code)
	{
		this(code, null);
	}

	/**
	 * Instantiates a new {@link ServiceExceptionDetail}.
	 * 
	 * @param code a standard error code specified per module type
	 * @param message a message explaining the error
	 */
	public ServiceExceptionDetail(final StandardServiceExceptionCode code, final String message)
	{
		super();
		this.code = code;
		this.message = message;
	}

	public String getMessage()
	{
		return (this.code == null ? "null" : this.code.toString()) + (this.message == null ? "" : ": " + this.message);
	}

	@Override
	public String toString()
	{
		return this.getMessage();
	}

	public int getCode()
	{
		return this.code == null ? 0 : this.code.getCode();
	}

	/**
	 * Overridden hashCode method which takes the message and an arbitrary hash as base.
	 */
	@Override
	public int hashCode()
	{
		int hash = ARBITRARY_HASH_7;
		hash = ARBITRARY_HASH_31 * hash + this.getCode();
		hash = ARBITRARY_HASH_31 * hash + (null == this.message ? 0 : this.message.hashCode());
		return hash;
	}

	/**
	 * Overridden equals that includes the comparison of service exception details.
	 * 
	 * @param obj an object
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if ((obj == null) || (obj.getClass() != this.getClass()))
		{
			return false;
		}

		final ServiceExceptionDetail other = (ServiceExceptionDetail) obj;
		return (this.getCode() == other.getCode())
				&& (this.message == other.getMessage() || this.message != null && this.message.equals(other.getMessage()));
	}

}
