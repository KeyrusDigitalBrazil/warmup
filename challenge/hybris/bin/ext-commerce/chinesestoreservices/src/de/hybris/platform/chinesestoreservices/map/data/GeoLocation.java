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
package de.hybris.platform.chinesestoreservices.map.data;

public class GeoLocation
{
	private String lng;
	private String lat;

	/**
	 * @return the lng
	 */
	public String getLng()
	{
		return lng;
	}

	/**
	 * @param lng
	 *           the lng to set
	 */
	public void setLng(final String lng)
	{
		this.lng = lng;
	}

	/**
	 * @return the lat
	 */
	public String getLat()
	{
		return lat;
	}

	/**
	 * @param lat
	 *           the lat to set
	 */
	public void setLat(final String lat)
	{
		this.lat = lat;
	}



}
