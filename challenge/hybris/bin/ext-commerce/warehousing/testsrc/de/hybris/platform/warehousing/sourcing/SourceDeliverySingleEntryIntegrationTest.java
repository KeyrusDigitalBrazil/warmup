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
package de.hybris.platform.warehousing.sourcing;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


@IntegrationTest
public class SourceDeliverySingleEntryIntegrationTest extends BaseSourcingIntegrationTest
{
	private static final Long CAMERA_QTY = new Long(5);
	private OrderModel order;
	Map<ProductModel, Long> expectedAllocation = new HashMap<ProductModel, Long>();

	@Before
	public void setUp() throws Exception
	{
		order = orders.Camera_Shipped(CAMERA_QTY);
	}

	@Test
	public void shouldSource_FromWarehouseWithHighestAvailability()
	{
		// Given
		setSourcingFactors(order.getStore(), 100, 0, 0, 0);
		stockLevels.Camera(warehouses.Montreal(), 100);
		stockLevels.Camera(warehouses.Boston(), 101);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation);
	}

	@Test
	public void shouldNotSource_StockForcedOutOfStock()
	{
		// Given
		setSourcingFactors(order.getStore(), 100, 0, 0, 0);

		StockLevelModel stockMontrealCamera = stockLevels.Camera(warehouses.Montreal(), 8);
		stockMontrealCamera.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertFalse(results.isComplete());
		assertEquals(0, results.getResults().size());
	}

	@Test
	public void shouldSource_StockForcedInStock()
	{
		// Given
		setSourcingFactors(order.getStore(), 100, 0, 0, 0);
		StockLevelModel stockMontrealCamera = stockLevels.Camera(warehouses.Montreal(), 0);
		stockMontrealCamera.setInStockStatus(InStockStatus.FORCEINSTOCK);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation);
	}

	@Test
	public void shouldSource_FromWarehouseWithHighestPriority()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 0, 100, 0);
		stockLevels.Camera(warehouses.Montreal(), 10);
		stockLevels.Camera(warehouses.Boston(), 10);

		warehouses.Montreal().setPriority(Integer.valueOf(50));
		warehouses.Boston().setPriority(Integer.valueOf(1));
		saveAll();

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
        expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation);
	}

	@Test
	public void shouldSource_FromWarehouseWithShortestDistanceMontreal()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		stockLevels.Camera(warehouses.Montreal(), 10);
		stockLevels.Camera(warehouses.Boston(), 50);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
        expectedAllocation.put(products.Camera(), CAMERA_QTY);
        assertSourcingResultContents(results, warehouses.Montreal(),expectedAllocation);
	}

	@Test
	public void shouldSource_FromWarehouseWithShortestDistanceBoston()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		stockLevels.Camera(warehouses.Montreal(), 50);
		stockLevels.Camera(warehouses.Boston(), 10);

		// When
		order.setDeliveryAddress(addresses.Boston());
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Boston(),expectedAllocation);
	}

	@Test
	public void shouldSource_FromWarehouseWithCombinedFactors()
	{
		/* In this test case, Montreal have a higher availability and a higher priority but the delivery address is in Boston
		So according to the weight of the sourcing factor, The chosen location must be Boston */

		// Given
		setSourcingFactors(order.getStore(), 30, 40, 30, 0);
		stockLevels.Camera(warehouses.Montreal(), 50);
		stockLevels.Camera(warehouses.Boston(), 20);

		warehouses.Montreal().setPriority(Integer.valueOf(10));
		warehouses.Boston().setPriority(Integer.valueOf(50));
		saveAll();

		// When
		order.setDeliveryAddress(addresses.Boston());
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation);
	}

	@Test
	public void shouldFailSource_InsufficientStock_SingleWarehouse()
	{
		// Given
		final Long availableStock = Long.valueOf(2);
		stockLevels.Camera(warehouses.Montreal(), availableStock.intValue());

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertFalse(results.isComplete());
		assertEquals(1, results.getResults().size());

        expectedAllocation.put(products.Camera(), availableStock);
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation);
	}

	@Test
	public void shouldFailSource_InsufficientStock_MultipleWarehouses()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 0, 100, 0);
		final Long availableStock = Long.valueOf(2);
		stockLevels.Camera(warehouses.Montreal(), availableStock.intValue());
		stockLevels.Camera(warehouses.Boston(), availableStock.intValue());

		warehouses.Montreal().setPriority(Integer.valueOf(1));
		warehouses.Boston().setPriority(Integer.valueOf(5));
		saveAll();

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertFalse(results.isComplete());
		assertEquals(2, results.getResults().size());
		expectedAllocation.put(products.Camera(), availableStock);
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation);
	}


	@Test
	public void shouldHandleException_WarehouseDoesNotHavePos()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);

		stockLevels.Camera(warehouses.Boston(), 5);
		stockLevels.Camera(warehouses.Random(), 2);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());

		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation);
	}

	@Test
	public void sourceFromWarehouse_WarehouseDoesNotHavePos()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);

		stockLevels.Camera(warehouses.Random(), 5);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());

		expectedAllocation.put(products.Camera(), CAMERA_QTY);
	}

	@Test
	public void sourceFromWarehouse_ProductNull()
	{
		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertFalse(results.isComplete());
	}
}
