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
package de.hybris.platform.chineselogisticservices.delivery.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeValueDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.util.PriceValue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseDeliveryServiceTest
{
	private static final String COUNTRY_ISO_CODE = "CN";
	private static final String CURRENCY_ISO_CODE = "zh";
	private static final Double DELIVERY_MODE_VALUE = 10D;
	private static final String COUNRTY_ISO_CODE_EN = "EN";

	@Mock
	private C2LItemZoneDeliveryModeValueDao c2LItemZoneDeliveryModeValueDao;

	private ChineseDeliveryService service;
	private CurrencyModel currency;
	private OrderModel order;
	private AddressModel address;
	private DistrictModel district;
	private CityModel city;
	private RegionModel region;
	private CountryModel country;
	private ZoneModel zone;
	private DeliveryModeModel deliveryMode;
	private ZoneDeliveryModeValueModel zoneDeliveryModeValue;
	private ZoneDeliveryModeModel ZoneDeliveryMode;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		service = new ChineseDeliveryService();
		service.setC2LItemZoneDeliveryModeValueDao(c2LItemZoneDeliveryModeValueDao);

		currency = new CurrencyModel();
		currency.setIsocode(CURRENCY_ISO_CODE);

		zone = new ZoneModel();
		deliveryMode = new DeliveryModeModel();

		district = new DistrictModel();
		district.setZone(zone);

		city = new CityModel();
		city.setZone(zone);

		region = new RegionModel();
		region.setZone(zone);

		country = new CountryModel();
		country.setIsocode(COUNTRY_ISO_CODE);
		country.setZone(zone);

		ZoneDeliveryMode = new ZoneDeliveryModeModel();
		ZoneDeliveryMode.setNet(Boolean.TRUE);

		zoneDeliveryModeValue = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValue.setValue(DELIVERY_MODE_VALUE);
		zoneDeliveryModeValue.setDeliveryMode(ZoneDeliveryMode);

		address = new AddressModel();
		address.setCountry(country);
		address.setCityDistrict(district);
		address.setCity(city);
		address.setRegion(region);

		order = new OrderModel();
		order.setDeliveryAddress(address);
		order.setCurrency(currency);

		BDDMockito.given(c2LItemZoneDeliveryModeValueDao.findDeliveryModeValueByC2LItem(district, order, deliveryMode)).willReturn(
				zoneDeliveryModeValue);
		BDDMockito.given(c2LItemZoneDeliveryModeValueDao.findDeliveryModeValueByC2LItem(city, order, deliveryMode))
				.willReturn(null);
		BDDMockito.given(c2LItemZoneDeliveryModeValueDao.findDeliveryModeValueByC2LItem(region, order, deliveryMode)).willReturn(
				null);
		BDDMockito.given(c2LItemZoneDeliveryModeValueDao.findDeliveryModeValueByC2LItem(country, order, deliveryMode)).willReturn(
				null);
	}

	@Test
	public void testGetDeliveryCostForDeliveryModeAndAbstractOrder()
	{
		final PriceValue priceValue = service.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryMode, order);
		Assert.assertNotNull(priceValue);
		Assert.assertEquals(CURRENCY_ISO_CODE, priceValue.getCurrencyIso());
		Assert.assertEquals(DELIVERY_MODE_VALUE, Double.valueOf(priceValue.getValue()));
		Assert.assertTrue(priceValue.isNet());
	}

	@Test
	public void testGetDeliveryCostForDeliveryModeAndAbstractOrderWithAddressNull()
	{
		order.setDeliveryAddress(null);
		final PriceValue priceValue = service.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryMode, order);
		Assert.assertNull(priceValue);
	}

}
