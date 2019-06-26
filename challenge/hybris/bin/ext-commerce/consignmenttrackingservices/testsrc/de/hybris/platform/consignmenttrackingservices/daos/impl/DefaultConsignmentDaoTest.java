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
package de.hybris.platform.consignmenttrackingservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.consignmenttrackingservices.daos.ConsignmentDao;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultConsignmentDaoTest extends ServicelayerTransactionalTest
{

	@Resource(name = "consignmentDao")
	private ConsignmentDao consignmentDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	private String orderCode;

	private String consignmentCode;

	@Before
	public void prepare()
	{

		final CustomerModel customer = new CustomerModel();
		customer.setUid(UUID.randomUUID().toString());

		final AddressModel shippingAddress = new AddressModel();
		shippingAddress.setOwner(customer);

		final VendorModel vendor = new VendorModel();
		vendor.setCode("v0001");
		vendor.setName("MockVendor", new Locale("en"));

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("wh0001");
		warehouse.setVendor(vendor);

		orderCode = "10001000";
		consignmentCode = "a10001000";

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("zh");
		currency.setSymbol("$");

		final UserModel user = new UserModel();
		user.setUid("testuser");

		final OrderModel order = new OrderModel();
		order.setCode(orderCode);
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setUser(user);

		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setCode(consignmentCode);
		consignment.setShippingAddress(shippingAddress);
		consignment.setWarehouse(warehouse);
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		consignment.setOrder(order);

		modelService.save(consignment);
	}

	@Test
	public void test_findConsignmentByCode()
	{
		final Optional<ConsignmentModel> consignment = consignmentDao.findConsignmentByCode(orderCode, consignmentCode);
		Assert.assertTrue(consignment.isPresent());
		Assert.assertEquals(consignmentCode, consignment.get().getCode());
	}

	@Test
	public void test_findConsignmentByOrder()
	{
		final List<ConsignmentModel> consignments = consignmentDao.findConsignmentsByOrder(orderCode);
		Assert.assertEquals(1, consignments.size());
		Assert.assertEquals(consignmentCode, consignments.get(0).getCode());
	}
}
