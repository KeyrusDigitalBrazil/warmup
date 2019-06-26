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
package de.hybris.platform.warehousing.warehouse.filter;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousesFilterProcessorTest
{

	@InjectMocks
	private WarehousesFilterProcessor warehousesFilterProcessor;

	@Mock
	private WarehousesFilter declineWarehousesFilter;
	@Mock
	private WarehousesFilter reservedWarehousesFilter;
	@Mock
	private WarehouseModel shippingWarehouse;
	@Mock
	private WarehouseModel declinedWarehouse;
	@Mock
	private WarehouseModel reservedWarehouse;

	private Set<WarehouseModel> allWarehouses;
	private Set<WarehouseModel> noDeclinedWarehouses;

	@Before
	public void setUp() throws Exception
	{
		allWarehouses = Sets.newHashSet(shippingWarehouse, declinedWarehouse, reservedWarehouse);
		noDeclinedWarehouses = Sets.newHashSet(shippingWarehouse, reservedWarehouse);
		warehousesFilterProcessor.setFilters(Lists.newArrayList(declineWarehousesFilter, reservedWarehousesFilter));
		when(declineWarehousesFilter.applyFilter(allWarehouses)).thenReturn(noDeclinedWarehouses);
		when(reservedWarehousesFilter.applyFilter(noDeclinedWarehouses)).thenReturn(Sets.newHashSet(shippingWarehouse));
	}

	@Test
	public void testProcessMultipleFilters() throws Exception
	{
		//When
		final Set<WarehouseModel> filteredWarehouses = warehousesFilterProcessor.filterLocations(allWarehouses);

		//Then
		assertEquals(1, filteredWarehouses.size());
		verify(declineWarehousesFilter).applyFilter(allWarehouses);
		verify(reservedWarehousesFilter).applyFilter(noDeclinedWarehouses);
	}

	@Test
	public void testProcessFilterWithEmptyWarehousesCollection()
	{
		//When
		warehousesFilterProcessor.filterLocations(Collections.emptySet());

		//Then
		verify(declineWarehousesFilter, never()).applyFilter(allWarehouses);
	}

	@Test
	public void testProcessFilterWithNullWarehousesCollection()
	{
		//When
		warehousesFilterProcessor.filterLocations(null);

		//Then
		verify(declineWarehousesFilter, never()).applyFilter(allWarehouses);
	}

	@Test
	public void testProcessFilterWithEmptyFiltersCollection()
	{
		//Given
		warehousesFilterProcessor.setFilters(Collections.EMPTY_LIST);

		//When
		warehousesFilterProcessor.filterLocations(allWarehouses);

		//Then
		verify(declineWarehousesFilter, never()).applyFilter(allWarehouses);
		verify(reservedWarehousesFilter, never()).applyFilter(noDeclinedWarehouses);
	}

	@Test
	public void testProcessFilterWithNullFiltersCollection()
	{
		//Given
		warehousesFilterProcessor.setFilters(null);

		//When
		warehousesFilterProcessor.filterLocations(allWarehouses);

		//Then
		verify(declineWarehousesFilter, never()).applyFilter(allWarehouses);
		verify(reservedWarehousesFilter, never()).applyFilter(noDeclinedWarehouses);
	}

}
