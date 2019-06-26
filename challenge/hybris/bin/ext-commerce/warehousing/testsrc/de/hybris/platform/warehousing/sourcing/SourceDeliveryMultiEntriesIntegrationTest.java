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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SourceDeliveryMultiEntriesIntegrationTest extends BaseSourcingIntegrationTest
{
	private static final Long CAMERA_QTY = new Long(5);
	private static final Long MEMORYCARD_QTY = new Long(4);
	Map<ProductModel, Long> expectedAllocation1 = new HashMap<ProductModel, Long>();
	Map<ProductModel, Long> expectedAllocation2 = new HashMap<ProductModel, Long>();

	private OrderModel order;

	@Before
	public void setUp() throws Exception
	{
		order = orders.CameraAndMemoryCard_Shipped(CAMERA_QTY, MEMORYCARD_QTY);
	}

	@Test
	public void shouldSource_NoSplit()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		stockLevels.Camera(warehouses.Montreal(), 15);
		stockLevels.Camera(warehouses.Boston(), 5);
		stockLevels.MemoryCard(warehouses.Montreal(), 2);
		stockLevels.MemoryCard(warehouses.Boston(), 5);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation1.put(products.Camera(), CAMERA_QTY);
		expectedAllocation1.put(products.MemoryCard(), MEMORYCARD_QTY);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation1);
	}

	@Test
	public void shouldSource_Split()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.MemoryCard(warehouses.Boston(), 5);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(2, results.getResults().size());
		expectedAllocation1.put(products.Camera(), CAMERA_QTY);
		expectedAllocation2.put(products.MemoryCard(), MEMORYCARD_QTY);
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation1);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation2);
	}

	@Test
	public void shouldSource_Split2()
	{
		// Given
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		stockLevels.Camera(warehouses.Boston(), 15);
		stockLevels.MemoryCard(warehouses.Montreal(), 2);
		stockLevels.MemoryCard(warehouses.Boston(), 2);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(2, results.getResults().size());
		expectedAllocation1.put(products.Camera(), CAMERA_QTY);
		expectedAllocation1.put(products.MemoryCard(), new Long(2));
		expectedAllocation2.put(products.MemoryCard(), new Long(2));
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation2);
		assertSourcingResultContents(results, warehouses.Boston(), expectedAllocation1);
	}
}
