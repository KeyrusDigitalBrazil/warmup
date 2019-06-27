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

import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ChineseC2LItemZoneDeliveryModeValueDaoTest extends ServicelayerTransactionalTest
{

	@Resource(name = "c2LItemZoneDeliveryModeValueDao")
	private ChineseC2LItemZoneDeliveryModeValueDao dao;

	@Resource
	private ModelService modelService;
	private ZoneDeliveryModeModel zdm;
	private ZoneDeliveryModeModel zdm2;
	private ZoneDeliveryModeValueModel val;
	private OrderModel order;
	private OrderModel order2;
	private CurrencyModel currency;
	private ZoneModel zone;
	private ZoneModel zone2;
	private CountryModel country;
	private CountryModel country2;
	private CustomerModel customer;

	private ZoneDeliveryModeModel createZoneDeliveryModeModel(String code, String name){
		ZoneDeliveryModeModel ZDM;
		ZDM = new ZoneDeliveryModeModel();
		ZDM.setCode(code);
		ZDM.setName(name, new Locale("en"));
		ZDM.setNet(Boolean.TRUE);
		ZDM.setActive(Boolean.TRUE);
		modelService.save(ZDM);
		return ZDM;
	}
	
	private ZoneModel createZoneModel(String code){
		ZoneModel ZONE;
		ZONE = new ZoneModel();
		ZONE.setCode(code);
		modelService.save(ZONE);
		return ZONE;
	}
	
	private CountryModel createCountryModel(String name,String Isocode,ZoneModel zone){
		CountryModel COUNTRY;
		COUNTRY = new CountryModel();
		COUNTRY.setName(name, new Locale("en"));
		COUNTRY.setIsocode(Isocode);
		COUNTRY.setZone(zone);
		modelService.save(COUNTRY);
		return COUNTRY;
	}
	
	private OrderModel createOrderModel(String code){
		OrderModel ORDER;
		ORDER = new OrderModel();
		ORDER.setCode(code);
		ORDER.setCurrency(currency);
		ORDER.setDate(new Date());
		ORDER.setNet(Boolean.TRUE);
		ORDER.setUser(customer);
		modelService.save(ORDER);
		return ORDER;
	}
	
	@Before
	public void prepare()
	{
		zdm = createZoneDeliveryModeModel("zdm","zdmName");
		zdm2 = createZoneDeliveryModeModel("","");
		
		zone = createZoneModel("south");
		zone2 = createZoneModel("");
		
		country = createCountryModel("china","zh",zone);
		country2 = createCountryModel("","",zone2);
		
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

		customer = new CustomerModel();
		customer.setUid("");

		order = createOrderModel("0000000001");
		order2 = createOrderModel("");
	}

	@Test
	public void testFindDeliveryModesByC2LItem()
	{
		final ZoneDeliveryModeValueModel result = dao.findDeliveryModeValueByC2LItem(country, order, zdm);
		Assert.assertEquals(val, result);
		final ZoneDeliveryModeValueModel result2 = dao.findDeliveryModeValueByC2LItem(country2, order2, zdm2);
		Assert.assertNull(result2);
	}

}
