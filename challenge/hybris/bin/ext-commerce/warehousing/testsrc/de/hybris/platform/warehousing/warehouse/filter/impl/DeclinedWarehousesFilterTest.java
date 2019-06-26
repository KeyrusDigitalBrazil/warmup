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
package de.hybris.platform.warehousing.warehouse.filter.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.model.SourcingBanModel;
import de.hybris.platform.warehousing.sourcing.ban.service.SourcingBanService;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeclinedWarehousesFilterTest
{
	@InjectMocks
	private DeclinedWarehousesFilter declinedWarehousesFilter;

	@Mock
	private WarehouseModel shippingWarehouse;
	@Mock
	private WarehouseModel declinedWarehouse;
	@Mock
	private SourcingBanModel sourcingBanForDeclinedWarehouse;
	@Mock
	private WarehouseModel reservedWarehouse;
	@Mock
	private SourcingBanService sourcingBanService;

	private Set<WarehouseModel> warehouses;

	@Before
	public void setUp() throws Exception
	{
		warehouses = Sets.newHashSet(shippingWarehouse, declinedWarehouse, reservedWarehouse);

		when(sourcingBanService.getSourcingBan(warehouses)).thenReturn(Collections.singletonList(sourcingBanForDeclinedWarehouse));
		when(sourcingBanForDeclinedWarehouse.getWarehouse()).thenReturn(declinedWarehouse);
	}

	@Test
	public void testDeclineWarehouseFilterWithDeclinedWarehouse()
	{
		//When
		final Set<WarehouseModel> filteredWarehouses = declinedWarehousesFilter.applyFilter(warehouses);

		//Then
		verify(sourcingBanService).getSourcingBan(warehouses);
		assertEquals(2, filteredWarehouses.size());
	}

	@Test
	public void testFilterWithNoDeclinedWarehouse()
	{
		//Given
		when(sourcingBanService.getSourcingBan(warehouses)).thenReturn(Collections.emptyList());

		//When
		final Set<WarehouseModel> filteredWarehouses = declinedWarehousesFilter.applyFilter(warehouses);

		//Then
		assertEquals(3, filteredWarehouses.size());
	}

	@Test
	public void testFilterWithEmptyWarehousesInput()
	{
		//When
		declinedWarehousesFilter.applyFilter(Collections.EMPTY_SET);

		verify(sourcingBanService, never()).getSourcingBan(warehouses);
	}

	@Test
	public void testFilterWithNullWarehousesInput()
	{
		//When
		declinedWarehousesFilter.applyFilter(null);

		verify(sourcingBanService, never()).getSourcingBan(warehouses);
	}
}
