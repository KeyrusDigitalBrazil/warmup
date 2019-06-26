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
package de.hybris.platform.addressservices.address;


import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;

import java.util.List;


/**
 * 
 * Provide methods to handle address related request
 *
 */
public interface AddressService extends de.hybris.platform.servicelayer.user.AddressService //NOSONAR
{
	/**
	 * Find cities by region code
	 *
	 * @param regionCode
	 *           region code
	 * @return city list in the region
	 */
	List<CityModel> getCitiesForRegion(String regionCode);

	/**
	 * Find districts by city code
	 *
	 * @param cityCode
	 *           city code
	 * @return district list in the city
	 */
	List<DistrictModel> getDistrictsForCity(String cityCode);

	/**
	 * Find city by its code
	 *
	 * @param isocode
	 *           city code
	 * @return city model
	 */
	CityModel getCityForIsocode(String isocode);

	/**
	 * Find district by its code
	 *
	 * @param isocode
	 *           district code
	 * @return district model
	 */
	DistrictModel getDistrictForIsocode(String isocode);
}
