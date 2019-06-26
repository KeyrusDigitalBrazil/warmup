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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class CisShippingRateOption
{
	/** The name of the delivery station. */
	@XmlElement(name = "deliveryStation")
	private String deliveryStation;

	/** The service type of the shipment. */
	@XmlElement(name = "serviceType")
	private CisShippingServiceLevel service;

	/** The total amount of the shipment. */
	@XmlElement(name = "totalAmount")
	private String totalAmount;

	/** The total weight of the shipment. */
	@XmlElement(name = "totalWeight")
	private String totalWeight;

	public String getDeliveryStation()
	{
		return this.deliveryStation;
	}

	public void setDeliveryStation(final String deliveryStation)
	{
		this.deliveryStation = deliveryStation;
	}

	public CisShippingServiceLevel getService()
	{
		return this.service;
	}

	public void setService(final CisShippingServiceLevel service)
	{
		this.service = service;
	}

	public String getTotalAmount()
	{
		return this.totalAmount;
	}

	public void setTotalAmount(final String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getTotalWeight()
	{
		return this.totalWeight;
	}

	public void setTotalWeight(final String totalWeight)
	{
		this.totalWeight = totalWeight;
	}
}
