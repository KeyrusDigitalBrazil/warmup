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

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisResult;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Result of a geolocation request.
 */
@XmlRootElement(name = "geolocationResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeoLocationResult extends CisResult
{
	/**
	 * Contains the resulting combinations of longitude and latitude for the given zipcodes.
	 */
	@XmlElement(name = "geoLocation")
	private List<CisAddress> geoLocations;

	public List<CisAddress> getGeoLocations()
	{
		return this.geoLocations;
	}

	public void setGeoLocations(final List<CisAddress> geoLocations)
	{
		this.geoLocations = geoLocations;
	}
}
