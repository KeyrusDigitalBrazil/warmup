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
package com.hybris.cis.client.avs.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


/**
 * Relates an address field to an error code.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisFieldError
{
	/** The field. */
	@XmlAttribute
	private CisField field;

	/** The error code. */
	@XmlElement
	private CisFieldErrorCode errorCode;


	/**
	 * Default constructor required by jaxb.
	 */
	public CisFieldError()
	{
		// required for jaxb
	}

	/**
	 * @param field field name in error
	 * @param errorCode detailed error code (missing, invalid etc...)
	 */
	public CisFieldError(final CisField field, final CisFieldErrorCode errorCode)
	{
		super();
		this.field = field == null ? CisField.UNKNOWN : field;
		this.errorCode = errorCode;
	}

	/**
	 * @return the field on which the error occurred
	 */
	public CisField getField()
	{
		return this.field;
	}

	/**
	 * @param field the erroneous field to set
	 */
	public void setField(final CisField field)
	{
		this.field = field;
	}

	/**
	 * @return the error code
	 */
	public CisFieldErrorCode getErrorCode()
	{
		return this.errorCode;
	}

	/**
	 * @param errorCode sets the error code in form of CisFieldErrorCode
	 */
	public void setErrorCode(final CisFieldErrorCode errorCode)
	{
		this.errorCode = errorCode;
	}

	@Override
	public String toString()
	{
		return "FieldError [field=" + this.field + ", errorCode=" + this.errorCode + "]";
	}

}
