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
package de.hybris.platform.chineselogisticservices.delivery.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ChineseC2LItemZoneDeliveryModeDaoTest extends ServicelayerTransactionalTest
{

	@Resource(name = "c2LItemZoneDeliveryModeDao")
	private ChineseC2LItemZoneDeliveryModeDao dao;

	@Resource
	private ModelService modelService;

	private ZoneDeliveryModeModel zdm;
	private ZoneDeliveryModeValueModel val;
	private OrderModel order;
	private CurrencyModel currency;
	private ZoneModel zone;
	private BaseStoreModel store;
	private CountryModel country;
	private CustomerModel customer;

	@Before
	public void prepare(){

		zdm = new ZoneDeliveryModeModel();
		zdm.setCode("zdm");
		zdm.setName("zdmName", new Locale("en"));
		zdm.setNet(Boolean.TRUE);
		zdm.setActive(Boolean.TRUE);
		modelService.save(zdm);

		zone = new ZoneModel();
		zone.setCode("south");
		modelService.save(zone);

		country = new CountryModel();
		country.setName("china", new Locale("en"));
		country.setIsocode("zh");
		country.setZone(zone);
		modelService.save(country);

		final Set<CountryModel> countries = new HashSet<>();
		countries.add(country);
		zone.setCountries(countries);

		currency = new CurrencyModel();
		currency.setName("USD", new Locale("en"));
		currency.setSymbol("$");
		currency.setIsocode("en");
		modelService.save(currency);

		val = new ZoneDeliveryModeValueModel();
		val.setDeliveryMode(zdm);
		val.setZone(zone);
		val.setCurrency(currency);
		val.setMinimum(1d);
		val.setValue(10d);
		modelService.save(val);

		store = new BaseStoreModel();
		store.setName("teststore", new Locale("en"));
		final Set<DeliveryModeModel> zdms = new HashSet<>();
		zdms.add(zdm);
		store.setDeliveryModes(zdms);
		store.setUid("teststore");
		modelService.save(store);

		customer = new CustomerModel();
		customer.setUid("");

		order = new OrderModel();
		order.setCode("0000000001");
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setNet(Boolean.TRUE);
		order.setStore(store);
		order.setUser(customer);
		modelService.save(order);
	}

	@Test
	public void testFindDeliveryModesByC2LItem(){
		final Collection<DeliveryModeModel> result = dao.findDeliveryModesByC2LItem(country, order);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(zdm, result.toArray()[0]);
	}
}
