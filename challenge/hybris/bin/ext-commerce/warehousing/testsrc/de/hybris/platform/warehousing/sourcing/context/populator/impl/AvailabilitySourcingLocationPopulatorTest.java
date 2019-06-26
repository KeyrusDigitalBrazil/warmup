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
 *
 */
package de.hybris.platform.warehousing.sourcing.context.populator.impl;

import com.google.common.collect.Lists;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingContext;
import de.hybris.platform.warehousing.data.sourcing.SourcingLocation;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AvailabilitySourcingLocationPopulatorTest
{
	private static final Long QUANTITY_1 = 4L;
	private static final Long QUANTITY_2 = 9L;

	private ProductModel product1 = new ProductModel();
	private ProductModel product2 = new ProductModel();


	@InjectMocks
	private final AvailabilitySourcingLocationPopulator populator = new AvailabilitySourcingLocationPopulator();
	private WarehouseModel warehouse;
	private SourcingLocation location;
	@Spy
	private OrderEntryModel orderEntry1;
	@Spy
	private OrderEntryModel orderEntry2;
	@Mock
	private DefaultWarehouseStockService warehouseStockService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private AtpFormulaModel atpFormula;

	@Before
	public void setUp()
	{
		location = new SourcingLocation();
		final SourcingContext context = new SourcingContext();
		final OrderModel order = new OrderModel();

		order.setStore(baseStoreModel);
		orderEntry1.setProduct(product1);
		orderEntry2.setProduct(product2);
		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2));

		product1.setCode("code1");
		product2.setCode("code2");

		context.setOrderEntries(Lists.newArrayList(orderEntry1, orderEntry2));
		location.setContext(context);

		warehouse = new WarehouseModel();

		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouse)).thenReturn(QUANTITY_1);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouse)).thenReturn(QUANTITY_2);
		when(baseStoreModel.getDefaultAtpFormula()).thenReturn(atpFormula);
		when(orderEntry1.getOrder()).thenReturn(order);
		when(orderEntry2.getOrder()).thenReturn(order);

	}

	@Test
	public void shouldPopulateAvailability()
	{
		populator.populate(warehouse, location);
		assertEquals(QUANTITY_1, location.getAvailability().get(product1));
		assertEquals(QUANTITY_2, location.getAvailability().get(product2));

	}

	@Test
	public void shouldDefaultAvailabilityToOrderEntryQuantityWhenNull()
	{
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouse)).thenReturn(QUANTITY_1);
		when(orderEntry1.getQuantityUnallocated()).thenReturn(QUANTITY_1);

		populator.populate(warehouse, location);
		assertEquals(QUANTITY_1, location.getAvailability().get(product1));
		assertEquals(QUANTITY_2, location.getAvailability().get(product2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailPopulate_NullSource()
	{
		warehouse = null;
		populator.populate(warehouse, location);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailPopulate_NullTarget()
	{
		location = null;
		populator.populate(warehouse, location);
	}
}
