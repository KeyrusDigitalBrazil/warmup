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
package de.hybris.platform.warehousing.sourcing.filter.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.warehouse.filter.WarehousesFilterProcessor;
import de.hybris.platform.warehousing.atp.strategy.impl.DefaultPickupWarehouseSelectionStrategy;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PickupSourcingLocationFilterTest
{
	@Mock
	private PointOfServiceModel posA;
	@Mock
	private PointOfServiceModel posB;
	@Mock
	private WarehouseModel priorityShippingPickupWarehouse;
	@Mock
	private WarehouseModel shippingPickupWarehouse;
	@Mock
	private WarehouseModel shippingWarehouse;
	@Mock
	private WarehouseModel pickupWarehouse;
	@Mock
	private WarehouseModel declinedShippingPickupWarehouse;
	@Mock
	private AbstractOrderModel order;
	@Mock
	private AbstractOrderEntryModel orderEntry1;
	@Mock
	private AbstractOrderEntryModel orderEntry2;
	@Mock
	private AbstractOrderEntryModel orderEntry3;
	@Mock
	private DeliveryModeModel priorityShipping;
	@Mock
	private DeliveryModeModel standardShipping;
	@Mock
	private PickUpDeliveryModeModel pickup;
	@Mock
	private WarehousesFilterProcessor warehousesFilterProcessor;

	private final PickupSourcingLocationFilter filter = new PickupSourcingLocationFilter();

	@Before
	public void setUp() throws Exception
	{
		final DefaultPickupWarehouseSelectionStrategy pickupWarehouseSelectionStrategy = new DefaultPickupWarehouseSelectionStrategy();
		pickupWarehouseSelectionStrategy.setWarehousesFilterProcessor(warehousesFilterProcessor);
		filter.setPickupWarehouseSelectionStrategy(pickupWarehouseSelectionStrategy);

		final Set<WarehouseModel> posAPickupWarehouses = Sets
				.newHashSet(priorityShippingPickupWarehouse, shippingPickupWarehouse, declinedShippingPickupWarehouse);
		final Set<WarehouseModel> posANonDeclinedPickupWarehouses = Sets
				.newHashSet(priorityShippingPickupWarehouse, shippingPickupWarehouse);
		when(warehousesFilterProcessor.filterLocations(posAPickupWarehouses)).thenReturn(posANonDeclinedPickupWarehouses);
		when(warehousesFilterProcessor.filterLocations(Sets.newHashSet(pickupWarehouse)))
				.thenReturn(Sets.newHashSet(pickupWarehouse));

		when(priorityShippingPickupWarehouse.getDeliveryModes()).thenReturn(Sets.newHashSet(priorityShipping, pickup));
		when(shippingPickupWarehouse.getDeliveryModes()).thenReturn(Sets.newHashSet(standardShipping, pickup));
		when(declinedShippingPickupWarehouse.getDeliveryModes()).thenReturn(Sets.newHashSet(standardShipping, pickup));
		when(pickupWarehouse.getDeliveryModes()).thenReturn(Sets.newHashSet(pickup));
		when(shippingWarehouse.getDeliveryModes()).thenReturn(Sets.newHashSet(standardShipping));

		when(posA.getWarehouses()).thenReturn(
				Lists.newArrayList(priorityShippingPickupWarehouse, shippingPickupWarehouse, shippingWarehouse,
						declinedShippingPickupWarehouse));
		when(posB.getWarehouses()).thenReturn(Lists.newArrayList(pickupWarehouse));

		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry1, orderEntry2, orderEntry3));
		when(orderEntry1.getDeliveryPointOfService()).thenReturn(posA);
		when(orderEntry2.getDeliveryPointOfService()).thenReturn(posB);
		when(orderEntry3.getDeliveryPointOfService()).thenReturn(null);
	}

	@Test
	public void shouldFindPickupLocations_PartialPickup()
	{
		final Collection<WarehouseModel> locations = filter.applyFilter(order, null);
		assertEquals(3, locations.size());
		assertTrue(locations.contains(priorityShippingPickupWarehouse));
		assertTrue(locations.contains(shippingPickupWarehouse));
		assertTrue(locations.contains(pickupWarehouse));
	}

	@Test
	public void shouldFindPickupLocations_AllPickup()
	{
		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry1, orderEntry2));
		final Collection<WarehouseModel> locations = filter.applyFilter(order, null);
		assertEquals(3, locations.size());
		assertTrue(locations.contains(priorityShippingPickupWarehouse));
		assertTrue(locations.contains(shippingPickupWarehouse));
		assertTrue(locations.contains(pickupWarehouse));
	}

	@Test
	public void shouldFindPickupLocations_SingleOrderEntry()
	{
		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry2));
		final Collection<WarehouseModel> locations = filter.applyFilter(order, null);
		assertEquals(1, locations.size());
		assertTrue(locations.contains(pickupWarehouse));
	}

	@Test
	public void shouldFindPickupLocations_NoPickup()
	{
		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry3));
		final Collection<WarehouseModel> locations = filter.applyFilter(order, null);
		assertEquals(0, locations.size());
	}

}
