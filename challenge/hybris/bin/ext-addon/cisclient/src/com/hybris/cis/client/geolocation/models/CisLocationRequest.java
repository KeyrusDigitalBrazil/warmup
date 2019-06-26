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
package com.hybris.cis.client.geolocation.models;

import com.hybris.cis.client.shared.models.AnnotationHashMap;
import com.hybris.cis.client.shared.models.CisAddress;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This request allows to query the CIS geolocation service and retrieve longitude / latitude of a set of given
 * addresses.
 */
@XmlRootElement(name = "cisLocationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisLocationRequest
{

	/**
	 * A set of addresses which should be resolved. At least a 2-letter country code and a zip code are required.
	 * Optionally, a city can be provided for a better matching.
	 */
	@XmlElement(name = "address")
	@XmlElementWrapper(name = "addresses")
	private List<CisAddress> addresses;

	/**
	 * Vendor specific values to pass in the request.
	 */
	@XmlElement(name = "vendorParameters")
	private AnnotationHashMap vendorParameters;

	public List<CisAddress> getAddresses()
	{
		return this.addresses;
	}

	public void setAddresses(final List<CisAddress> addresses)
	{
		this.addresses = addresses;
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
