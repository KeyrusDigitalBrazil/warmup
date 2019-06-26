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
package com.hybris.cis.client.shipping.models;

import com.hybris.cis.client.shared.models.CisResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * Container for shipping method.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisShippingMethod extends CisResult
{
	/** The level of shipping service. */
	private CisShippingServiceLevel level;

	/** The service method code. */
	private String methodCode;

	/** The service method name. */
	private String methodName;

	public CisShippingServiceLevel getLevel()
	{
		return this.level;
	}

	public void setLevel(final CisShippingServiceLevel level)
	{
		this.level = level;
	}

	public String getMethodCode()
	{
		return this.methodCode;
	}

	public void setMethodCode(final String methodCode)
	{
		this.methodCode = methodCode;
	}

	public String getMethodName()
	{
		return this.methodName;
	}

	public void setMethodName(final String methodName)
	{
		this.methodName = methodName;
	}
}
