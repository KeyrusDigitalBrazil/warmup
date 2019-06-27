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
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SourceDeliveryHugeEntriesIntegrationTest extends BaseSourcingIntegrationTest
{
	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void shouldSource_Split50Times()
	{
		// Given
		final OrderModel order = orders.Camera_Shipped(new Long(1000));
		setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		for (int i = 0; i < 500; i++)
			stockLevels.Camera(warehouses.Random(), 2);
		// When
		final SourcingResults results = sourcingService.sourceOrder(order);
		assertTrue(results.isComplete());
		assertEquals(500, results.getResults().size());
	}
}
