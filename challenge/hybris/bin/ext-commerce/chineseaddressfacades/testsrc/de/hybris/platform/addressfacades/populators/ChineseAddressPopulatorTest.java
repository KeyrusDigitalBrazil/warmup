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
package de.hybris.platform.addressfacades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.addressfacades.data.DistrictData;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.addressservices.strategies.NameWithTitleFormatStrategy;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;


@UnitTest
public class ChineseAddressPopulatorTest
{
	private static final String CITY_ISOCODE = "CN-11-1";
	private static final String DISTRICT_ISOCODE = "CN-11-1-1";
	private static final String CITY_NAME = "Beijing";
	private static final String DISTRICT_NAME = "Dongcheng";
	private static final String CHINESE_FIRST_NAME = "建中";
	private static final String CHINESE_LAST_NAME = "张";
	private static final String CHINESE_FULLNAME = "张建中";
	private static final String MR_TITLE_NAME = "Mr";
	private static final String ENGLISH_ISOCODE = "en";
	private final AddressModel addressModel = new AddressModel();
	@Mock
	private I18NService i18NService;
	@Mock
	private NameWithTitleFormatStrategy nameWithTitleFormatStrategy;

	private ChineseAddressPopulator chineseAddressPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		chineseAddressPopulator = new ChineseAddressPopulator();
		ReflectionTestUtils.setField(chineseAddressPopulator, "i18NService", i18NService);
		ReflectionTestUtils.setField(chineseAddressPopulator, "nameWithTitleFormatStrategy", nameWithTitleFormatStrategy);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateAddressDataWithSourceNull()
	{
		final AddressModel addressModel = null;
		final AddressData addressData = new AddressData();
		chineseAddressPopulator.populate(addressModel, addressData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateAddressDataWithTargetNull()
	{
		final AddressData addressData = null;
		chineseAddressPopulator.populate(addressModel, addressData);
	}

	@Test
	public void testPopulateAddressDataWithNullFullNameAndEmptyCellPhone()
	{
		final CityModel cityModel = Mockito.spy(new CityModel());
		final DistrictModel districtModel = Mockito.spy(new DistrictModel());
		final Locale currentLocale = new Locale(ENGLISH_ISOCODE);
		initializeCommonData(cityModel, districtModel, addressModel, currentLocale);

		addressModel.setFirstname(CHINESE_FIRST_NAME);
		addressModel.setLastname(CHINESE_LAST_NAME);

		final AddressData addressData = new AddressData();
		final String expectedFullName = MR_TITLE_NAME + " " + CHINESE_LAST_NAME + " " + CHINESE_FIRST_NAME;

		Mockito.when(i18NService.getCurrentLocale()).thenReturn(currentLocale);
		Mockito.when(nameWithTitleFormatStrategy.getFullnameWithTitle(CHINESE_FIRST_NAME, CHINESE_LAST_NAME, MR_TITLE_NAME))
				.thenReturn(expectedFullName);
		Mockito.doReturn(CITY_NAME).when(cityModel).getName();
		Mockito.doReturn(DISTRICT_NAME).when(districtModel).getName();
		chineseAddressPopulator.populate(addressModel, addressData);

		Assert.assertEquals(CITY_ISOCODE, addressData.getCity().getCode());
		Assert.assertEquals(CITY_NAME, addressData.getCity().getName());
		Assert.assertEquals(DISTRICT_ISOCODE, addressData.getDistrict().getCode());
		Assert.assertEquals(DISTRICT_NAME, addressData.getDistrict().getName());
		Assert.assertEquals(expectedFullName, addressData.getFullnameWithTitle());
		Assert.assertNull(addressData.getCellphone());
	}

	@Test
	public void testPopulateAddressDataWithExistingFullNameAndCellPhone()
	{
		final CityModel cityModel = Mockito.spy(new CityModel());
		final DistrictModel districtModel = Mockito.spy(new DistrictModel());
		final Locale currentLocale = new Locale(ENGLISH_ISOCODE);
		initializeCommonData(cityModel, districtModel, addressModel, currentLocale);

		addressModel.setFullname(CHINESE_FULLNAME);
		final String cellPhone = "1898989533";
		addressModel.setCellphone(cellPhone);

		final AddressData addressData = new AddressData();
		final String expectedFullName = MR_TITLE_NAME + CHINESE_FULLNAME;

		Mockito.when(i18NService.getCurrentLocale()).thenReturn(currentLocale);
		Mockito.when(nameWithTitleFormatStrategy.getFullnameWithTitle(CHINESE_FULLNAME, MR_TITLE_NAME))
				.thenReturn(expectedFullName);
		Mockito.doReturn(CITY_NAME).when(cityModel).getName();
		Mockito.doReturn(DISTRICT_NAME).when(districtModel).getName();
		chineseAddressPopulator.populate(addressModel, addressData);

		Assert.assertEquals(CITY_ISOCODE, addressData.getCity().getCode());
		Assert.assertEquals(CITY_NAME, addressData.getCity().getName());
		Assert.assertEquals(DISTRICT_ISOCODE, addressData.getDistrict().getCode());
		Assert.assertEquals(DISTRICT_NAME, addressData.getDistrict().getName());
		Assert.assertEquals(expectedFullName, addressData.getFullnameWithTitle());
		Assert.assertEquals(cellPhone, addressData.getCellphone());
	}

	@Test
	public void testExtractCityWithCityNull()
	{
		final CityData cityData = chineseAddressPopulator.extractCity(addressModel);
		Assert.assertNull(cityData.getCode());
		Assert.assertNull(cityData.getName());
	}

	@Test
	public void testExtractCityWithCityNotNull()
	{
		final CityModel cityModel = Mockito.spy(new CityModel());
		cityModel.setIsocode(CITY_ISOCODE);
		addressModel.setCity(cityModel);

		Mockito.doReturn(CITY_NAME).when(cityModel).getName();
		final CityData cityData = chineseAddressPopulator.extractCity(addressModel);

		Assert.assertEquals(CITY_ISOCODE, cityData.getCode());
		Assert.assertEquals(CITY_NAME, cityData.getName());
	}

	@Test
	public void testExtractDistrictWithDistrictNull()
	{
		final DistrictData districtData = chineseAddressPopulator.extractDistrict(addressModel);
		Assert.assertNull(districtData.getCode());
		Assert.assertNull(districtData.getName());
	}

	@Test
	public void testExtractDistrictWithDistrictNotNull()
	{
		final DistrictModel districtModel = Mockito.spy(new DistrictModel());
		districtModel.setIsocode(DISTRICT_ISOCODE);
		addressModel.setCityDistrict(districtModel);

		Mockito.doReturn(DISTRICT_NAME).when(districtModel).getName();
		final DistrictData districtData = chineseAddressPopulator.extractDistrict(addressModel);

		Assert.assertEquals(DISTRICT_ISOCODE, districtData.getCode());
		Assert.assertEquals(DISTRICT_NAME, districtData.getName());
	}

	@Test
	public void testExtractTitleName()
	{
		final TitleModel titleModel = new TitleModel();
		final Locale locale = new Locale(ENGLISH_ISOCODE);
		titleModel.setName(MR_TITLE_NAME, locale);

		Mockito.when(i18NService.getCurrentLocale()).thenReturn(locale);

		Assert.assertEquals(MR_TITLE_NAME, chineseAddressPopulator.extractTitleName(titleModel));
	}

	protected void initializeCommonData(CityModel cityModel, DistrictModel districtModel, AddressModel addressModel, Locale locale)
	{
		cityModel.setIsocode(CITY_ISOCODE);
		districtModel.setIsocode(DISTRICT_ISOCODE);

		final TitleModel titleModel = new TitleModel();
		titleModel.setName(MR_TITLE_NAME, locale);

		addressModel.setTitle(titleModel);
		addressModel.setCity(cityModel);
		addressModel.setCityDistrict(districtModel);
	}
}
