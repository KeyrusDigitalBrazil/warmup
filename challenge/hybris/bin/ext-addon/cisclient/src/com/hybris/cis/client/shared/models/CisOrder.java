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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * An order containing addresses and line items and may represent an order, a shipment or a return depending on the
 * context.
 */
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisOrder implements Identifiable
{
	/** Unique id (e.g order/shipment/return number). */
	@XmlAttribute(name = "id")
	private String id;

	/** Date used for records (e.g. order/shipping/return date). */
	@XmlElement(name = "date")
	private Date date;

	/** 3 letter ISO 4217 currency code. */
	@XmlElement(name = "currency")
	private String currency;

	/** List of addresses. */
	@XmlElementWrapper(name = "addresses")
	@XmlElement(name = "address")
	private List<CisAddress> addresses = new ArrayList<CisAddress>();

	/** List of line items. */
	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "lineItem")
	private List<CisLineItem> lineItems;

	/**
	 * Vendor specific values to pass in the request.
	 */
	@XmlElement(name = "vendorParameters")
	private AnnotationHashMap vendorParameters;

	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return this.date == null ? null : new Date(this.date.getTime());
	}

	public void setDate(final Date date)
	{
		this.date = date == null ? null : new Date(date.getTime());
	}

	public List<CisAddress> getAddresses()
	{
		return this.addresses;
	}

	public void setAddresses(final List<CisAddress> shipments)
	{
		this.addresses = shipments;
	}

	public List<CisLineItem> getLineItems()
	{
		return this.lineItems;
	}

	public void setLineItems(final List<CisLineItem> lineItems)
	{
		this.lineItems = lineItems;
	}

	public String getCurrency()
	{
		return this.currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	/**
	 * Returns the first address of the given type.
	 * 
	 * @param type The type you're interested in
	 * @return An address or null
	 */
	public CisAddress getAddressByType(final CisAddressType type)
	{
		for (final CisAddress cisAddress : this.getAddresses())
		{
			if (type.equals(cisAddress.getType()))
			{
				return cisAddress;
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		final StringBuilder value = new StringBuilder();
		value.append("CisOrder [id=").append(this.getId()).append("]");
		return value.toString();
	}

	public AnnotationHashMap getVendorParameters()
	{
		return this.vendorParameters;
	}

	public void setVendorParameters(final AnnotationHashMap vendorParameters)
	{
		this.vendorParameters = vendorParameters;
	}
}
