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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultB2BOrderThresholdTimespanEvaluationStrategyTest
{
	private CurrencyModel testCurrency;

	@Before
	public void setup() throws Exception
	{
		testCurrency = new CurrencyModel();
		testCurrency.setIsocode("Test");
	}

	@Test
	public void shouldGetPermissionToEvaluate()
	{
		final DefaultB2BOrderThresholdTimespanEvaluationStrategy defaultB2BOrderThresholdTimespanEvaluationStrategy = new DefaultB2BOrderThresholdTimespanEvaluationStrategy();

		final B2BOrderThresholdTimespanPermissionModel b2bPermissionModel1 = new B2BOrderThresholdTimespanPermissionModel();
		b2bPermissionModel1.setThreshold(Double.valueOf("10.25"));
		b2bPermissionModel1.setCurrency(testCurrency);
		final B2BOrderThresholdTimespanPermissionModel b2bPermissionModel2 = new B2BOrderThresholdTimespanPermissionModel();
		b2bPermissionModel2.setThreshold(Double.valueOf("10.45"));
		final CurrencyModel otherCurrency = new CurrencyModel();
		otherCurrency.setIsocode("other");
		b2bPermissionModel2.setCurrency(otherCurrency); // Different currency from order
		final B2BOrderThresholdTimespanPermissionModel b2bPermissionModel3 = new B2BOrderThresholdTimespanPermissionModel();
		b2bPermissionModel3.setThreshold(Double.valueOf("10.35")); // Highest threshold. Should be returned
		b2bPermissionModel3.setCurrency(testCurrency);
		final B2BOrderThresholdTimespanPermissionModel b2bPermissionModel4 = new B2BOrderThresholdTimespanPermissionModel();
		b2bPermissionModel4.setThreshold(Double.valueOf("10.15"));
		b2bPermissionModel4.setCurrency(testCurrency);

		final Set<B2BOrderThresholdTimespanPermissionModel> permissions = new HashSet<>();
		permissions.add(b2bPermissionModel1);
		permissions.add(b2bPermissionModel2);
		permissions.add(b2bPermissionModel3);
		permissions.add(b2bPermissionModel4);

		final AbstractOrderModel order = new OrderModel();
		order.setCurrency(testCurrency);
		final B2BOrderThresholdPermissionModel permissionToEvaluate = defaultB2BOrderThresholdTimespanEvaluationStrategy
				.getPermissionToEvaluate(permissions, order);
		Assert.assertEquals("Unexpected permission to evaluate", b2bPermissionModel3, permissionToEvaluate);
	}
}
