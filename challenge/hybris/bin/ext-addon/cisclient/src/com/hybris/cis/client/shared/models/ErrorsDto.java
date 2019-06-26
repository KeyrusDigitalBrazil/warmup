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

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Root element used for all exceptions.
 */
@XmlRootElement(name = "errors")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorsDto
{
	/** The service id. */
	@XmlAttribute
	private String serviceId;

	/** List of line items. */
	@XmlElement(name = "error")
	private List<ErrorDto> errors;

	/**
	 * Instantiates an empty ErrorsDto object.
	 */
	public ErrorsDto()
	{
		super();
	}

	/**
	 * Instantiates an ErrorsDto object with service id and details.
	 * 
	 * @param serviceId the service Id
	 * @param detail the error details
	 */
	public ErrorsDto(final String serviceId, final ErrorDto detail)
	{
		this(serviceId, Collections.singletonList(detail));
	}

	/**
	 * Instantiates an ErrorsDto.
	 * 
	 * @param serviceId the service id
	 * @param details A list of error details
	 */
	public ErrorsDto(final String serviceId, final List<ErrorDto> details)
	{
		super();
		this.errors = details;
		this.serviceId = serviceId;
	}

	public List<ErrorDto> getErrors()
	{
		return this.errors;
	}

	public void setErrors(final List<ErrorDto> errors)
	{
		this.errors = errors;
	}

	public String getServiceId()
	{
		return this.serviceId;
	}

	public void setServiceId(final String serviceId)
	{
		this.serviceId = serviceId;
	}

	@Override
	public String toString()
	{
		final StringBuilder sbr = new StringBuilder();
		sbr.append('[').append(this.serviceId).append("] Error details: ");

		final List<ErrorDto> dtls = this.getErrors();
		if (dtls != null)
		{
			for (final ErrorDto detail : dtls)
			{
				sbr.append("\n").append(detail.getMessage());
			}
		}

		return sbr.toString();
	}



}
