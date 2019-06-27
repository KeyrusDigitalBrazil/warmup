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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.sourcing.filter.SourcingFilterProcessor;
import de.hybris.platform.warehousing.sourcing.filter.SourcingFilterResultOperator;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import de.hybris.platform.warehousing.warehouse.service.WarehousingWarehouseService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
public class AvailabilitySourcingLocationFilterTest
{
	private static final Long TEN = Long.valueOf(10);
	private static final Long ZERO = Long.valueOf(0);

	private static final String COUNTRY_CA = "CA";
	private static final String PRODUCT_CODE_1 = "product1";
	private static final String PRODUCT_CODE_2 = "product2";

	@Mock
	private WarehouseModel warehouseA;
	@Mock
	private WarehouseModel warehouseB;
	@Mock
	private WarehouseModel warehouseC;
	@Mock
	private WarehouseModel warehouseD;

	private AvailabilitySourcingLocationFilter filterA;
	private SourcingFilterProcessor processor;

	@Mock
	private DefaultWarehouseStockService warehouseStockService;
	@Mock
	private WarehousingWarehouseService warehouseService;
	@Mock
	private AbstractOrderModel order;
	@Mock
	private AbstractOrderEntryModel orderEntry1;
	@Mock
	private AbstractOrderEntryModel orderEntry2;
	@Mock
	private ProductModel product1;
	@Mock
	private ProductModel product2;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private AtpFormulaModel atpFormula;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		filterA = new AvailabilitySourcingLocationFilter();
		filterA.setWarehouseStockService(warehouseStockService);

		when(product1.getCode()).thenReturn(PRODUCT_CODE_1);
		when(product2.getCode()).thenReturn(PRODUCT_CODE_2);

		// Availability results
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseA)).thenReturn(TEN);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseB)).thenReturn(TEN);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseC)).thenReturn(ZERO);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseA)).thenReturn(TEN);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseB)).thenReturn(TEN);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseC)).thenReturn(ZERO);

		final AddressModel address = Mockito.mock(AddressModel.class);
		final CountryModel country = Mockito.mock(CountryModel.class);
		when(order.getDeliveryAddress()).thenReturn(address);
		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry1, orderEntry2));
		when(orderEntry1.getProduct()).thenReturn(product1);
		when(orderEntry2.getProduct()).thenReturn(product2);
		when(address.getCountry()).thenReturn(country);
		when(country.getIsocode()).thenReturn(COUNTRY_CA);
		when(orderEntry1.getOrder()).thenReturn(order);
		when(orderEntry2.getOrder()).thenReturn(order);
		when(order.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getDefaultAtpFormula()).thenReturn(atpFormula);

		processor = new SourcingFilterProcessor();
	}

	@Test
	public void testAtsFilter()
	{
		filterA.setFilterResultOperator(SourcingFilterResultOperator.AND);
		final Set<WarehouseModel> locations = Sets.newHashSet(warehouseA, warehouseB, warehouseC);
		filterA.filterLocations(order, locations);

		assertEquals(2, locations.size());
		assertTrue(locations.contains(warehouseA));
		assertTrue(locations.contains(warehouseB));
	}

	@Test
	public void testAllocationFilterWithForcedInStockLocation()
	{
		//Given
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseD)).thenReturn(null);

		//When
		filterA.setFilterResultOperator(SourcingFilterResultOperator.AND);
		final Set<WarehouseModel> locations = Sets.newHashSet(warehouseA, warehouseB,warehouseD);
		filterA.filterLocations(order, locations);

		//Then
		assertEquals(3, locations.size());
		assertTrue(locations.contains(warehouseA));
		assertTrue(locations.contains(warehouseB));
		assertTrue(locations.contains(warehouseD));
	}

	@Test
	public void testIsWarehouseHasAvailabilityForAnyProductInOrder_WhenNoAvailability()
	{
		//Given
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseA)).thenReturn(0L);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseA)).thenReturn(0L);

		//When
		final boolean isAvailable = filterA.isWarehouseHasAvailabilityForAnyProductInOrder(order, warehouseA);

		//Then
		assertFalse(isAvailable);
	}

	@Test
	public void testIsWarehouseHasAvailabilityForAnyProductInOrder_WhenForcedInStockAvailability()
	{
		//Given
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseA)).thenReturn(null);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseA)).thenReturn(0L);

		//When
		final boolean isAvailable = filterA.isWarehouseHasAvailabilityForAnyProductInOrder(order, warehouseA);

		//Then
		assertTrue(isAvailable);
	}

	@Test
	public void testIsWarehouseHasAvailabilityForAnyProductInOrder_WhenAvailable()
	{
		//Given
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product1.getCode(), warehouseA)).thenReturn(10L);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(product2.getCode(), warehouseA)).thenReturn(0L);

		//When
		final boolean isAvailable = filterA.isWarehouseHasAvailabilityForAnyProductInOrder(order, warehouseA);

		//Then
		assertTrue(isAvailable);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyFilterWithNullOrder()
	{
		filterA.setFilterResultOperator(SourcingFilterResultOperator.NONE);
		processor.setFilters(Lists.newArrayList(filterA));
		processor.filterLocations(null, Collections.<WarehouseModel> emptySet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyFilterWithNullLocation()
	{
		filterA.setFilterResultOperator(SourcingFilterResultOperator.NONE);
		processor.setFilters(Lists.newArrayList(filterA));
		processor.filterLocations(order, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApplyFilterWithNullFilterResultOperator() throws Exception
	{
		processor.setFilters(Lists.newArrayList(filterA));
		processor.afterPropertiesSet();
		processor.filterLocations(order, Collections.<WarehouseModel> emptySet());
	}
}
