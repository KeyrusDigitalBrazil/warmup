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
 */
package de.hybris.platform.warehousing.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ordercancel.OrderCancelCompleteTest;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Re-implements test {@link OrderCancelCompleteTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = OrderCancelCompleteTest.class)
public class OrderManagementOrderCancelCompleteTest extends OrderCancelCompleteTest
{
	@Override
	@Test
	public void testWarehouseResponsePartialCancelOK() throws Exception
	{
		// This scenario is impossible to get with order management as we are checking the order status before accepting a shipping confirmation for a consignment
		assertTrue(true);
	}
}
