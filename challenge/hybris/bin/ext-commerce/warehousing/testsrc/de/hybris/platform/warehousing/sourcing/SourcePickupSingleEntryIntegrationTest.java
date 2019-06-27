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
package de.hybris.platform.warehousing.sourcing;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


@IntegrationTest
public class SourcePickupSingleEntryIntegrationTest extends BaseSourcingIntegrationTest
{
	private static final Long CAMERA_QTY = new Long(5);
	Map<ProductModel, Long> expectedAllocation = new HashMap<ProductModel, Long>();
	private OrderModel order;

	@Before
	public void setUp() throws Exception
	{
		order = orders.Camera_PickupInMontreal(CAMERA_QTY);
	}

	@Test
	public void shouldSourceFromDesignatedPickupLocation()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 10);
		stockLevels.Camera(warehouses.Boston(), 10);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertTrue(results.isComplete());
		assertEquals(1, results.getResults().size());
		expectedAllocation.put(products.Camera(), CAMERA_QTY);
		assertSourcingResultContents(results, warehouses.Montreal(), expectedAllocation);
	}

	@Test
	public void shouldFailSource_NoStock()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 0);

		// When
		final SourcingResults results = sourcingService.sourceOrder(order);

		// Then
		assertFalse(results.isComplete());
		assertTrue(results.getResults().isEmpty());
	}

	@Test
	public void shouldFailSource_InsufficientStock()
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

}
