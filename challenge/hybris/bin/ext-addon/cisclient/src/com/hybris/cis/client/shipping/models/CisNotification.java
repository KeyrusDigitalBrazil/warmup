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


/**
 * Contains notifications configuration for a shipment.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisNotification
{
	/**
	 * Email to notify.
	 */
	@XmlElement(name = "email")
	private String email;

	/**
	 * Notify on delivery.
	 */
	@XmlElement(name = "onDelivery")
	private Boolean onDelivery;

	/**
	 * Notify on shipment.
	 */
	@XmlElement(name = "onShipment")
	private Boolean onShipment;

	/**
	 * Notify on payment.
	 */
	@XmlElement(name = "onTender")
	private Boolean onTender;

	/**
	 * Notify on exception.
	 */
	@XmlElement(name = "onException")
	private Boolean onException;

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public Boolean getOnDelivery()
	{
		return this.onDelivery;
	}

	public void setOnDelivery(final Boolean onDelivery)
	{
		this.onDelivery = onDelivery;
	}

	public Boolean getOnShipment()
	{
		return this.onShipment;
	}

	public void setOnShipment(final Boolean onShipment)
	{
		this.onShipment = onShipment;
	}

	public Boolean getOnTender()
	{
		return this.onTender;
	}

	public void setOnTender(final Boolean onTender)
	{
		this.onTender = onTender;
	}

	public Boolean getOnException()
	{
		return this.onException;
	}

	public void setOnException(final Boolean onException)
	{
		this.onException = onException;
	}
}
