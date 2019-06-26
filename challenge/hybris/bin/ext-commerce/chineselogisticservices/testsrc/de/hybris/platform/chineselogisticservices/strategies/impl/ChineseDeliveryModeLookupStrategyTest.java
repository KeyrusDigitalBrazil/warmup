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
package de.hybris.platform.chineselogisticservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.addressservices.model.CityModel;
import de.hybris.platform.addressservices.model.DistrictModel;
import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeDao;
import de.hybris.platform.commerceservices.delivery.dao.PickupDeliveryModeDao;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseDeliveryModeLookupStrategyTest
{
	private static final String COUNTRY_ISO_CODE = "CN";

	private ChineseDeliveryModeLookupStrategy strategy;

	@Mock
	private C2LItemZoneDeliveryModeDao c2LItemZoneDeliveryModeDao;
	@Mock
	private PickupDeliveryModeDao pickupDeliveryModeDao;

	private OrderModel order;
	private AbstractOrderEntryModel orderEntry;
	private PointOfServiceModel pointOfService;
	private DeliveryModeModel deliveryMode;
	private AddressModel address;
	private DistrictModel district;
	private CityModel city;
	private RegionModel region;
	private CountryModel country;
	private CurrencyModel currency;
	private BaseStoreModel store;
	private ZoneModel zone;

	private List<AbstractOrderEntryModel> orderEntries;
	private List<DeliveryModeModel> deliveryModes;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new ChineseDeliveryModeLookupStrategy();
		strategy.setC2LItemZoneDeliveryModeDao(c2LItemZoneDeliveryModeDao);
		strategy.setPickupDeliveryModeDao(pickupDeliveryModeDao);

		currency = new CurrencyModel();
		pointOfService = new PointOfServiceModel();

		orderEntry = new OrderEntryModel();
		orderEntries = new ArrayList<>();
		orderEntries.add(orderEntry);

		deliveryMode = new DeliveryModeModel();
		deliveryModes = new ArrayList<>();
		deliveryModes.add(deliveryMode);

		zone = new ZoneModel();

		district = new DistrictModel();
		district.setZone(zone);

		city = new CityModel();
		region = new RegionModel();
		country = new CountryModel();
		country.setIsocode(COUNTRY_ISO_CODE);

		address = new AddressModel();
		address.setCityDistrict(district);
		address.setCity(city);
		address.setRegion(region);
		address.setCountry(country);

		store = new BaseStoreModel();

		order = new OrderModel();
		order.setEntries(orderEntries);
		order.setDeliveryAddress(address);
		order.setStore(store);
		order.setCurrency(currency);

		BDDMockito.given(pickupDeliveryModeDao.findPickupDeliveryModesForAbstractOrder(order)).willReturn(deliveryModes);
		BDDMockito.given(c2LItemZoneDeliveryModeDao.findDeliveryModesByC2LItem(district, order)).willReturn(deliveryModes);
		BDDMockito.given(c2LItemZoneDeliveryModeDao.findDeliveryModesByC2LItem(city, order)).willReturn(null);
		BDDMockito.given(c2LItemZoneDeliveryModeDao.findDeliveryModesByC2LItem(region, order)).willReturn(null);
		BDDMockito.given(c2LItemZoneDeliveryModeDao.findDeliveryModesByC2LItem(country, order)).willReturn(null);
	}

	@Test
	public void testGetSelectableDeliveryModesForOrderWithPickUpOnly()
	{
		orderEntry.setDeliveryPointOfService(pointOfService);
		final List<DeliveryModeModel> list = strategy.getSelectableDeliveryModesForOrder(order);
		Assert.assertEquals(deliveryMode, list.get(0));
	}

	@Test
	public void testGetSelectableDeliveryModesForOrderWithNotPickUpOnly()
	{
		final List<DeliveryModeModel> list = strategy.getSelectableDeliveryModesForOrder(order);
		Assert.assertEquals(deliveryMode, list.get(0));
	}

	@Test
	public void testGetSelectableDeliveryModesForOrderWithDeliveryAddressNull()
	{
		order.setDeliveryAddress(null);
		final List<DeliveryModeModel> list = strategy.getSelectableDeliveryModesForOrder(order);

		Assert.assertEquals(list, Collections.emptyList());

	}

	@Test(expected = NullPointerException.class)
	public void testGetSelectableDeliveryModesForOrderWithStoreNull()
	{
		order.setStore(null);
		final List<DeliveryModeModel> list = strategy.getSelectableDeliveryModesForOrder(order);

	}
}
