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
package com.hybris.cis.client.shared.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


/**
 * Represents a service exception.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDto
{
	/** The error code. */
	@XmlAttribute
	private String code;

	/** The error message. */
	@XmlValue
	private String message;

	/**
	 * Default constructor required by jaxb.
	 */
	public ErrorDto()
	{
		// needed for jaxb
	}

	public ErrorDto(final String code, final String message)
	{
		super();
		this.code = code;
		this.message = message;
	}

	public String getCode()
	{
		return this.code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return this.message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

}
