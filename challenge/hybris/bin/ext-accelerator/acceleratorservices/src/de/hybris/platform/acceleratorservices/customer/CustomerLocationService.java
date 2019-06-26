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
package de.hybris.platform.acceleratorservices.customer;

import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.storelocator.model.PointOfServiceModel;


/**
 * Defines an API to handle a user's location
 */
public interface CustomerLocationService
{
	/**
	 * Stores the information of user location. If search term is given, but gps is null it tries to determine GPS
	 * coordinates for the given search term.
	 * 
	 * @param userLocationData
	 */
	void setUserLocation(UserLocationData userLocationData);

	/**
	 * Returns user stored location or null if no location found for current session user.
	 * 
	 * @return stored location
	 */
	UserLocationData getUserLocation();


	/**
	 * Calculates distance between given origin point and given point of service data object
	 * 
	 * @param origin
	 * @param pointOfServiceModel
	 * @return distance between given points
	 */
	double calculateDistance(GeoPoint origin, PointOfServiceModel pointOfServiceModel);
}
