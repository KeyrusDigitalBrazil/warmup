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
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import junit.framework.Assert;


@UnitTest
public class CityPopulatorTest
{
	private static final String CITY_ISOCODE = "CN-11-1";
	private static final String CITY_NAME = "Beijing";
	@Mock
	I18NService i18NService;

	private CityPopulator cityPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cityPopulator = new CityPopulator();
		ReflectionTestUtils.setField(cityPopulator, "i18NService", i18NService);
	}

	@Test
	public void testCityPopulator()
	{
		final C2LItemModel cityModel = new CityModel();
		cityModel.setIsocode(CITY_ISOCODE);

		final Locale englishLocale = new Locale("en");
		cityModel.setName(CITY_NAME, englishLocale);

		final CityData cityData = new CityData();
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(englishLocale);

		cityPopulator.populate(cityModel, cityData);

		Assert.assertEquals(CITY_ISOCODE, cityData.getCode());
		Assert.assertEquals(CITY_NAME, cityData.getName());
	}
}
