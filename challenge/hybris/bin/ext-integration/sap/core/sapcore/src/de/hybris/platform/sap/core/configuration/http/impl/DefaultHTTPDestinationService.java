/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.configuration.http.impl;

import de.hybris.platform.sap.core.configuration.http.HTTPDestination;
import de.hybris.platform.sap.core.configuration.http.HTTPDestinationService;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation which returns a HTTP Destination.
 */
public class DefaultHTTPDestinationService implements HTTPDestinationService
{
	/**
	 * HTTP Destinations.
	 */
	private Map<String, HTTPDestination> httpDestinations = new HashMap<String, HTTPDestination>();

	/**
	 * Sets the HTTPDestinations. Used for Spring Bean injection.
	 * 
	 * @param httpDestinations
	 *           map of http destinations
	 */
	public void setHTTPDestinations(final Map<String, HTTPDestination> httpDestinations)
	{
		this.httpDestinations = httpDestinations;
	}

	@Override
	public HTTPDestination getHTTPDestination(final String destinationName)
	{
		HTTPDestination httpDestination = httpDestinations.get(destinationName);
		if (httpDestination == null)
		{
			httpDestination = new DefaultHTTPDestination();
		}
		return httpDestination;
	}

}
