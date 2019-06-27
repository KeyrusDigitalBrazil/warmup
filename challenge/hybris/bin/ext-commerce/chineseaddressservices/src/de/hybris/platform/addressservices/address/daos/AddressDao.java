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
package de.hybris.platform.addressservices.address.daos;


import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;

import java.util.List;


public interface AddressDao extends de.hybris.platform.servicelayer.user.daos.AddressDao //NOSONAR
{
	/**
	 * Find cities by region code
	 *
	 * @param regionCode
	 * @return cities
	 */
	List<CityModel> getCitiesForRegion(String regionCode);

	/**
	 * Find districts by city code
	 *
	 * @param cityCode
	 * @return districts
	 */
	List<DistrictModel> getDistrictsForCity(String cityCode);

	/**
	 * Find city by its code
	 *
	 * @param isocode
	 * @return city
	 */
	CityModel getCityForIsocode(String isocode);

	/**
	 * Find district by its code
	 *
	 * @param isocode
	 * @return district
	 */
	DistrictModel getDistrictForIsocode(String isocode);
}
