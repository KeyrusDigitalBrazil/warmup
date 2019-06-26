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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "shippingRateResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisShippingRateResult extends CisResult
{
	/** List of shipping rates available. */
	@XmlElementWrapper(name = "serviceOptions")
	@XmlElement(name = "option")
	private List<CisShippingRateOption> options;

	public List<CisShippingRateOption> getOptions()
	{
		return this.options;
	}

	public void setOptions(final List<CisShippingRateOption> options)
	{
		this.options = options;
	}
}
