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
package de.hybris.platform.addressservices.address.daos.impl;

import de.hybris.platform.addressservices.address.daos.AddressDao;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultAddressDao;

import java.util.List;


public class ChineseAddressDao extends DefaultAddressDao implements AddressDao
{
	@Override
	public List<CityModel> getCitiesForRegion(final String regionCode)
	{
		final String queryString = "SELECT {c:" + CityModel.PK + "}" + "FROM {" + CityModel._TYPECODE + " AS c JOIN "
				+ RegionModel._TYPECODE + " AS r " + "ON {r:" + RegionModel.PK + "} = {c:" + CityModel.REGION + "}  " + " AND {r:"
				+ RegionModel.ISOCODE + "} =?paramRegionCode }";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramRegionCode", regionCode);
		return getFlexibleSearchService().<CityModel> search(query).getResult();
	}

	@Override
	public List<DistrictModel> getDistrictsForCity(final String cityCode)
	{
		final String queryString = "SELECT {d:" + DistrictModel.PK + "}" + "FROM {" + DistrictModel._TYPECODE + " AS d JOIN "
				+ CityModel._TYPECODE + " AS c " + "ON {c:" + CityModel.PK + "} = {d:" + DistrictModel.CITY + "}  " + " AND {c:"
				+ CityModel.ISOCODE + "} =?paramCityCode }";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramCityCode", cityCode);
		return getFlexibleSearchService().<DistrictModel> search(query).getResult();
	}

	@Override
	public CityModel getCityForIsocode(final String isocode)
	{
		final String queryString = "SELECT {c:" + CityModel.PK + "}" + "FROM {" + CityModel._TYPECODE + " AS c } " + "WHERE "
				+ "{c:" + CityModel.ISOCODE + "}=?paramCityCode ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramCityCode", isocode);

		final SearchResult<CityModel> result = getFlexibleSearchService().<CityModel> search(query);
		if (result == null || result.getCount() == 0)
		{
			return null;
		}

		return result.getResult().get(0);
	}

	@Override
	public DistrictModel getDistrictForIsocode(final String isocode)
	{
		final String queryString = "SELECT {d:" + DistrictModel.PK + "} " + "FROM {" + DistrictModel._TYPECODE + " AS d} "
				+ "WHERE {d:" + DistrictModel.ISOCODE + "}=?paramDistrictCode";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("paramDistrictCode", isocode);

		final SearchResult<DistrictModel> result = getFlexibleSearchService().<DistrictModel> search(query);
		if (result == null || result.getCount() == 0)
		{
			return null;
		}

		return result.getResult().get(0);
	}

}
