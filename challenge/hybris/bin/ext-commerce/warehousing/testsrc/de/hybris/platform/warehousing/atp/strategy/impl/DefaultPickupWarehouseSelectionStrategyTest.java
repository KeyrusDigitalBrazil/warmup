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
package de.hybris.platform.warehousing.atp.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.warehouse.filter.WarehousesFilterProcessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPickupWarehouseSelectionStrategyTest
{
	@InjectMocks
	private DefaultPickupWarehouseSelectionStrategy pickupWarehouseSelectionStrategy;
	@Mock
	private PointOfServiceModel pos;
	@Mock
	private WarehouseModel warehouseNoPickup;
	@Mock
	private WarehouseModel warehouseForPickup;
	@Mock
	private DeliveryModeModel internationalShipping;
	@Mock
	private DeliveryModeModel localShipping;
	@Mock
	private PickUpDeliveryModeModel pickup;
	@Mock
	private WarehousesFilterProcessor warehousesFilterProcessor;

	@Before
	public void setUp()
	{
		when(pos.getWarehouses()).thenReturn(Arrays.asList(warehouseNoPickup));
		when(warehouseForPickup.getDeliveryModes()).thenReturn(Sets.newHashSet(localShipping, pickup));
		when(warehouseNoPickup.getDeliveryModes()).thenReturn(Sets.newHashSet(internationalShipping, localShipping));

		when(warehousesFilterProcessor.filterLocations(Sets.newHashSet(warehouseNoPickup))).thenReturn(Collections.emptySet());
		when(warehousesFilterProcessor.filterLocations(Sets.newHashSet(warehouseNoPickup, warehouseForPickup)))
				.thenReturn(Collections.singleton(warehouseForPickup));
		when(warehousesFilterProcessor.filterLocations(Sets.newHashSet(warehouseForPickup)))
				.thenReturn(Collections.singleton(warehouseForPickup));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_nullPos()
	{
		//When
		pickupWarehouseSelectionStrategy.getWarehouses(null);
	}

	@Test
	public void shouldGetWarehouses_nullWarehouses()
	{
		//Given
		when(pos.getWarehouses()).thenReturn(null);

		//When
		final Collection<WarehouseModel> warehouses = pickupWarehouseSelectionStrategy.getWarehouses(pos);

		//Then
		assertTrue(warehouses.isEmpty());
	}

	@Test
	public void shouldGetWarehouses_nullDeliveryModes()
	{
		//Given
		when(warehouseNoPickup.getDeliveryModes()).thenReturn(null);

		//When
		final Collection<WarehouseModel> warehouses = pickupWarehouseSelectionStrategy.getWarehouses(pos);

		//Then
		assertTrue(warehouses.isEmpty());
	}

	@Test
	public void shouldGetWarehouses_noPickup()
	{
		//When
		final Collection<WarehouseModel> warehouses = pickupWarehouseSelectionStrategy.getWarehouses(pos);

		//Then
		assertTrue(warehouses.isEmpty());
	}

	@Test
	public void shouldGetWarehousesValid()
	{
		//Given
		when(pos.getWarehouses()).thenReturn(Arrays.asList(warehouseForPickup, warehouseNoPickup));

		//When
		final Collection<WarehouseModel> warehouses = pickupWarehouseSelectionStrategy.getWarehouses(pos);

		//Then
		assertEquals(1, warehouses.size());
		assertTrue(warehouses.contains(warehouseForPickup));
	}
}
