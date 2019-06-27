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
package de.hybris.platform.commerceservices.storefinder.helpers;

import de.hybris.platform.store.BaseStoreModel;


/**
 * Helper class provides unit recalculation
 */
public interface DistanceHelper
{

	/**
	 * Gets the distance string for given distance value and the distance unit that is set in the base store model. In no
	 * distance unit is set in the store model default 'km' is assumed.
	 * 
	 * @param baseStoreModel
	 *           the base store model that provide distance unit
	 * @param distanceInKm
	 *           the distance in km
	 * @return the calculated distance string
	 */
	String getDistanceStringForStore(BaseStoreModel baseStoreModel, double distanceInKm);


	/**
	 * Gets the distance string for given distance value and the distance unit that is set in corresponding base store
	 * model. If no distance unit is set in the store model default 'km' is assumed.
	 * 
	 * @param locationName
	 *           the location name
	 * @param distanceInKm
	 *           the distance in km
	 * @return the calculated distance string
	 */
	String getDistanceStringForLocation(String locationName, double distanceInKm);
}
