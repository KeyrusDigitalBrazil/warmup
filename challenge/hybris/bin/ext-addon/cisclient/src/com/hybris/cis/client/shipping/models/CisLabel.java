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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Container for labels.
 */
@XmlRootElement(name = "label")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisLabel extends CisResult
{
	/** The data for a label. */
	@XmlElement(name = "data")
	private String data;

	public String getData()
	{
		return this.data;
	}

	public void setData(final String data)
	{
		this.data = data;
	}
}
