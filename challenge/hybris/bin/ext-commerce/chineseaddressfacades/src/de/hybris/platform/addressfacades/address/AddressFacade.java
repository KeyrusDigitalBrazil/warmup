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
package de.hybris.platform.addressfacades.address;


import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;

import java.util.List;


/**
 * 
 * Provide method related to address data
 *
 */
public interface AddressFacade
{
	/**
	 * Find city by its code
	 *
	 * @param isocode
	 *           city code
	 * @return city data
	 */
	CityData getCityForIsocode(String isocode);

	/**
	 * Find district by its code
	 *
	 * @param isocode
	 *           district code
	 * @return district data
	 */
	DistrictData getDistrcitForIsocode(String isocode);

	/**
	 * Find cities by region code
	 *
	 * @param regionCode
	 *           region Code
	 * @return city list in the region
	 */
	List<CityData> getCitiesForRegion(String regionCode);

	/**
	 * Find districts by city code
	 *
	 * @param cityCode
	 *           city Code
	 * @return district list in the city
	 */
	List<DistrictData> getDistrictsForCity(String cityCode);


	/**
	 * Validate the specific postcode
	 *
	 * @param postcode
	 *           the specific postcode
	 * @return validated result
	 */
	boolean validatePostcode(String postcode);
}
