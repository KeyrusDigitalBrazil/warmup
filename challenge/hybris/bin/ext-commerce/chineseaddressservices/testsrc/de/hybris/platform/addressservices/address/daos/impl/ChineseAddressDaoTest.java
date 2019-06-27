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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;



/**
 *
 */
@IntegrationTest
public class ChineseAddressDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private ChineseAddressDao chineseAddressDao;

	@Resource
	private ModelService modelService;

	private final String COUNTRY_ISOCODE = "CN";
	private final String REGION_ISOCODE = "CN-11";
	private final String CITY_ISOCODE = "CN-11-1";
	private final String CITY = "Beijing";
	private final String DISTRICT_ISOCODE1 = "CN-11-1-3";
	private final String DISTRICT_ISOCODE2 = "CN-11-1-1";
	private final String DISTRICT1 = "DaXing";
	private final String DISTRICT2 = "DongCheng";

	@Before
	public void prepare()
	{
		final CountryModel country = new CountryModel();
		country.setIsocode(COUNTRY_ISOCODE);

		final RegionModel region = new RegionModel();
		region.setIsocode(REGION_ISOCODE);
		region.setCountry(country);

		final CityModel city = new CityModel();
		city.setRegion(region);
		city.setIsocode(CITY_ISOCODE);
		city.setName(CITY, Locale.ENGLISH);

		final DistrictModel district1 = new DistrictModel();
		district1.setCity(city);
		district1.setName(DISTRICT1, Locale.ENGLISH);
		district1.setIsocode(DISTRICT_ISOCODE1);

		final DistrictModel district2 = new DistrictModel();
		district2.setCity(city);
		district2.setName(DISTRICT2, Locale.ENGLISH);
		district2.setIsocode(DISTRICT_ISOCODE2);

		modelService.save(district1);
		modelService.save(district2);
		modelService.refresh(city);
		modelService.refresh(region);
	}

	@Test
	public void test_Get_Cities_For_Region()
	{
		final List<CityModel> cities = chineseAddressDao.getCitiesForRegion(REGION_ISOCODE);
		assertEquals(1, cities.size());
		assertEquals(CITY_ISOCODE, cities.get(0).getIsocode());
	}


	@Test
	public void test_Get_City_For_Isocode()
	{
		final CityModel cityTest = chineseAddressDao.getCityForIsocode(CITY_ISOCODE);
		assertEquals(CITY, cityTest.getName());
		assertEquals(CITY_ISOCODE, cityTest.getIsocode());
	}

	@Test
	public void test_Get_District_For_Isocode()
	{
		final DistrictModel district = chineseAddressDao.getDistrictForIsocode(DISTRICT_ISOCODE1);
		assertEquals(DISTRICT1, district.getName());
		assertEquals(DISTRICT_ISOCODE1, district.getIsocode());
		assertEquals(CITY_ISOCODE, district.getCity().getIsocode());
	}

	@Test
	public void test_Get_Districts_For_City()
	{
		final List<DistrictModel> districts = new ArrayList<>(chineseAddressDao.getDistrictsForCity(CITY_ISOCODE));
		districts.sort(Comparator.comparing(DistrictModel::getName));

		assertEquals(2, districts.size());

		assertEquals(DISTRICT1, districts.get(0).getName());
		assertEquals(DISTRICT_ISOCODE1, districts.get(0).getIsocode());
		assertEquals(CITY_ISOCODE, districts.get(0).getCity().getIsocode());

		assertEquals(DISTRICT2, districts.get(1).getName());
		assertEquals(DISTRICT_ISOCODE2, districts.get(1).getIsocode());
		assertEquals(CITY_ISOCODE, districts.get(1).getCity().getIsocode());
	}

	@Test
	public void testGetCityForIsocodeNull()
	{
		final String CITY_ISOCODE_NULL = "not exist";
		final CityModel cityTest = chineseAddressDao.getCityForIsocode(CITY_ISOCODE_NULL);

		assertNull(cityTest);
	}

	@Test
	public void testGetDistrictForIsocodeNull()
	{
		final String DISTRICT_ISOCODE_NULL = "not exsit";
		final DistrictModel district = chineseAddressDao.getDistrictForIsocode(DISTRICT_ISOCODE_NULL);
		assertNull(district);
	}

}
