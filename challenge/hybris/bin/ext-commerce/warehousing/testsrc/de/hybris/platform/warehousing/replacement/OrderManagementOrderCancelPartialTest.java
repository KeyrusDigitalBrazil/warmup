/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordercancel.OrderCancelPartialTest;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Re-implements test {@link OrderCancelPartialTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = OrderCancelPartialTest.class)
public class OrderManagementOrderCancelPartialTest extends OrderCancelPartialTest
{
	@Resource
	protected UserService userService;

	@Resource
	private DefaultOrderCancelService orderCancelService;

	@Override
	@Test
	public void testSomeCancelableOrderEntriesWithConsignments()
	{
		final UserModel user = userService.getCurrentUser();
		final AddressModel deliveryAddress = new AddressModel();
		deliveryAddress.setOwner(user);
		deliveryAddress.setFirstname("Juergen");
		deliveryAddress.setLastname("Albertsen");
		deliveryAddress.setTown("Muenchen");
		getModelService().saveAll(Arrays.asList(deliveryAddress));

		BaseStoreModel basestore = new BaseStoreModel();
		basestore.setUid("testStore");
		basestore.setName("test", Locale.ENGLISH);
		getModelService().save(basestore);

		VendorModel vendor = new VendorModel();
		vendor.setCode("testVendor");
		getModelService().save(vendor);

		WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("testWarehouse");
		warehouse.setBaseStores(Lists.newArrayList(basestore));
		warehouse.setVendor(vendor);
		getModelService().save(warehouse);

		final ConsignmentModel consignment1 = createConsignmentModel("Consignment1", deliveryAddress, warehouse,
				ConsignmentStatus.READY);
		final ConsignmentModel consignment2 = createConsignmentModel("Consignment2", deliveryAddress, warehouse,
				ConsignmentStatus.SHIPPED);
		final ConsignmentModel consignment3 = createConsignmentModel("Consignment3", deliveryAddress, warehouse,
				ConsignmentStatus.SHIPPED);

		final ConsignmentEntryModel consignmentEntry1 = createConsignmentEntry(consignment1, getOrder().getEntries().get(0), 1L);
		final ConsignmentEntryModel consignmentEntry2 = createConsignmentEntry(consignment2, getOrder().getEntries().get(1), 2L);
		final ConsignmentEntryModel consignmentEntry3 = createConsignmentEntry(consignment1, getOrder().getEntries().get(2), 1L);
		final ConsignmentEntryModel consignmentEntry4 = createConsignmentEntry(consignment3, getOrder().getEntries().get(2), 2L);

		consignment1.setConsignmentEntries(Sets.newHashSet(consignmentEntry1, consignmentEntry3));
		getModelService().save(consignment1);
		consignment2.setConsignmentEntries(Sets.newHashSet(consignmentEntry2));
		getModelService().save(consignment2);
		consignment3.setConsignmentEntries(Sets.newHashSet(consignmentEntry4));
		getModelService().save(consignment3);

		getOrder().getEntries().get(0).setConsignmentEntries(Sets.newHashSet(consignmentEntry1));
		getOrder().getEntries().get(1).setConsignmentEntries(Sets.newHashSet(consignmentEntry2));
		getOrder().getEntries().get(2).setConsignmentEntries(Sets.newHashSet(consignmentEntry3, consignmentEntry4));

		assertTrue(this.getOrder().getEntries().size() >= 3);
		assertEquals("Order should have 3 consignments:", 3, this.getOrder().getConsignments().size());

		final Map<AbstractOrderEntryModel, Long> cancelableEntries = orderCancelService
				.getAllCancelableEntries(this.getOrder(), null);

		assertTrue(cancelableEntries.containsKey(getOrder().getEntries().get(0)));
		assertFalse(cancelableEntries.containsKey(getOrder().getEntries().get(1)));
		assertTrue(cancelableEntries.containsKey(getOrder().getEntries().get(2)));

		assertEquals("All quantity of entry1 should be cancelable", Long.valueOf(1), cancelableEntries.get(getOrder().getEntries().get(0)));
		assertEquals("Only 1 item of entry3 should be cancelable as 2 are already shipped", Long.valueOf(1),
				cancelableEntries.get(getOrder().getEntries().get(2)));
	}

	@Override
	@Test
	public void testWarehouseResponsePartialCancelPartialOK() throws Exception
	{
		// This scenario is impossible to get with order management as we are checking the order status before accepting a shipping confirmation for a consignment
		assertTrue(true);
	}

	protected ConsignmentModel createConsignmentModel(String code, AddressModel deliveryAddress, WarehouseModel warehouse,
			ConsignmentStatus status)
	{
		ConsignmentModel myConsignment = new ConsignmentModel();
		myConsignment.setOrder(getOrder());
		myConsignment.setCode(code);
		myConsignment.setShippingAddress(deliveryAddress);
		myConsignment.setStatus(status);
		myConsignment.setWarehouse(warehouse);
		getModelService().save(myConsignment);
		return myConsignment;
	}

	protected ConsignmentEntryModel createConsignmentEntry(ConsignmentModel consignment, AbstractOrderEntryModel orderEntry, Long qty)
	{
		final ConsignmentEntryModel consignmentEntryModel = new ConsignmentEntryModel();
		consignmentEntryModel.setOrderEntry(orderEntry);
		consignmentEntryModel.setQuantity(qty);
		consignmentEntryModel.setConsignment(consignment);
		getModelService().save(consignmentEntryModel);
		return consignmentEntryModel;
	}
}
