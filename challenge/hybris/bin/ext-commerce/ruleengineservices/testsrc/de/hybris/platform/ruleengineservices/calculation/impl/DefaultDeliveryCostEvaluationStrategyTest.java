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
package de.hybris.platform.ruleengineservices.calculation.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultDeliveryCostEvaluationStrategyTest
{

	private DefaultDeliveryCostEvaluationStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new DefaultDeliveryCostEvaluationStrategy();
	}

	@Test
	public void testEvaluateCost() throws Exception
	{
		final AbstractOrderModel order = null;
		final DeliveryModeModel deliveryMode = null;
		final BigDecimal evaluateCost = strategy.evaluateCost(order, deliveryMode);
		assertThat(evaluateCost, is(BigDecimal.ZERO));
	}

}
