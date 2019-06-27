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
package de.hybris.platform.addressfacades.address.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.addressservices.address.AddressService;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class ChineseAddressFacadeTest
{
	private static final String CITY_ISO_CODE = "CN-11-1";

	private static final String DISTRICT_ISO_CODE = "CN-11-1-1";

	@Mock
	private AddressService chineseAddressService;
	@Mock
	private Converter<CityModel, CityData> cityConverter;
	@Mock
	private Converter<DistrictModel, DistrictData> districtConverter;

	private ChineseAddressFacade chineseAddressFacade;

	private CityModel cityModel;

	private DistrictModel districtModel;

	private CityData expectedCityData;

	private DistrictData expectedDistrictData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		chineseAddressFacade = new ChineseAddressFacade();
		chineseAddressFacade.setChineseAddressService(chineseAddressService);
		chineseAddressFacade.setCityConverter(cityConverter);
		chineseAddressFacade.setDistrictConverter(districtConverter);
		cityModel = new CityModel();
		cityModel.setIsocode(CITY_ISO_CODE);
		districtModel = new DistrictModel();
		districtModel.setIsocode(DISTRICT_ISO_CODE);
	}

	@Test
	public void testGetCityWhenExistCityWithIsocode()
	{
		when(chineseAddressService.getCityForIsocode(CITY_ISO_CODE)).thenReturn(cityModel);
		Mockito.doAnswer(new Answer<CityData>()
		{
			@Override
			public CityData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				expectedCityData = (CityData) args[1];
				return null;
			}
		}).when(cityConverter).convert(Mockito.any(CityModel.class), Mockito.any(CityData.class));

		final CityData citDataActual = chineseAddressFacade.getCityForIsocode(CITY_ISO_CODE);

		Assert.assertSame(expectedCityData, citDataActual);
	}

	@Test
	public void testGetCityWhenNonexistCityWithIsoCode()
	{
		when(chineseAddressService.getCityForIsocode(CITY_ISO_CODE)).thenReturn(null);
		final CityData citDataActual = chineseAddressFacade.getCityForIsocode(CITY_ISO_CODE);

		Assert.assertNull(citDataActual);
	}

	@Test
	public void testGetRegionWhenExistRegionWithIsocode()
	{
		when(chineseAddressService.getDistrictForIsocode(DISTRICT_ISO_CODE)).thenReturn(districtModel);
		Mockito.doAnswer(new Answer<DistrictData>()
		{
			@Override
			public DistrictData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				expectedDistrictData = (DistrictData) args[1];
				return null;
			}
		}).when(districtConverter).convert(Mockito.any(DistrictModel.class), Mockito.any(DistrictData.class));
		final DistrictData districtDataActual = chineseAddressFacade.getDistrcitForIsocode(DISTRICT_ISO_CODE);

		Assert.assertSame(expectedDistrictData, districtDataActual);
	}

	@Test
	public void testGetRegionWhenNonexistRegionWithIsocode()
	{
		when(chineseAddressService.getDistrictForIsocode(DISTRICT_ISO_CODE)).thenReturn(null);
		final DistrictData districtDataActual = chineseAddressFacade.getDistrcitForIsocode(DISTRICT_ISO_CODE);

		Assert.assertNull(districtDataActual);
	}

	@Test
	public void testGetCitiesForRegion()
	{
		final List<CityModel> cityModels = new ArrayList<>();
		cityModels.add(cityModel);
		Mockito.doReturn(cityModels).when(chineseAddressService).getCitiesForRegion(CITY_ISO_CODE);
		Mockito.doAnswer(new Answer<CityData>()
		{
			@Override
			public CityData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				expectedCityData = (CityData) args[1];
				return null;
			}
		}).when(cityConverter).convert(Mockito.any(CityModel.class), Mockito.any(CityData.class));
		final List<CityData> cityDataListActual = chineseAddressFacade.getCitiesForRegion(CITY_ISO_CODE);

		final List<CityData> expectedCityDataList = new ArrayList<>();
		expectedCityDataList.add(expectedCityData);
		Assert.assertEquals(expectedCityDataList, cityDataListActual);
	}

	@Test
	public void testEmptyGetCitiesForRegion()
	{
		Mockito.doReturn(Collections.emptyList()).when(chineseAddressService).getCitiesForRegion(Mockito.any());
		final List<CityData> cityDataListActual = chineseAddressFacade.getCitiesForRegion(CITY_ISO_CODE);
		Assert.assertTrue(cityDataListActual.isEmpty());
	}

	@Test
	public void testGetDistrictsForCity()
	{
		final List<DistrictModel> districtModels = new ArrayList<>();
		districtModels.add(districtModel);
		Mockito.doReturn(districtModels).when(chineseAddressService).getDistrictsForCity(DISTRICT_ISO_CODE);
		Mockito.doAnswer(new Answer<DistrictData>()
		{
			@Override
			public DistrictData answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				expectedDistrictData = (DistrictData) args[1];
				return null;
			}
		}).when(districtConverter).convert(Mockito.any(DistrictModel.class), Mockito.any(DistrictData.class));
		final List<DistrictData> districtDataListActual = chineseAddressFacade.getDistrictsForCity(DISTRICT_ISO_CODE);

		final List<DistrictData> expectedDistrictDataList = new ArrayList<>();
		expectedDistrictDataList.add(expectedDistrictData);
		Assert.assertEquals(expectedDistrictDataList, districtDataListActual);
	}

	@Test
	public void testEmptyGetDistrictsForCity()
	{
		Mockito.doReturn(Collections.emptyList()).when(chineseAddressService).getDistrictsForCity(Mockito.any());
		final List<DistrictData> districtDataListActual = chineseAddressFacade.getDistrictsForCity(DISTRICT_ISO_CODE);
		Assert.assertTrue(districtDataListActual.isEmpty());
	}
}
