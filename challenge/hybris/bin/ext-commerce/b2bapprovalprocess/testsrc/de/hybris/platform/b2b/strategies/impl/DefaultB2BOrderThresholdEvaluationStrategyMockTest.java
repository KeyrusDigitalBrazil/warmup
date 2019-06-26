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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class DefaultB2BOrderThresholdEvaluationStrategyMockTest extends HybrisMokitoTest
{

	DefaultB2BOrderThresholdEvaluationStrategy defaultB2BOrderThresholdEvaluationStrategy;

	@Mock
	private ModelService mockModelService;

	@Before
	public void setUp() throws Exception
	{
		defaultB2BOrderThresholdEvaluationStrategy = new DefaultB2BOrderThresholdEvaluationStrategy();
		defaultB2BOrderThresholdEvaluationStrategy.setModelService(mockModelService);
	}

	@Test
	public void testGetPermissionsToEvaluate()
	{
		final CurrencyModel usdCurrency = mock(CurrencyModel.class);
		when(usdCurrency.getIsocode()).thenReturn("USD");

		// Stubbing does not seem to work have to ceate concrete objects.
		final B2BOrderThresholdPermissionModel lowThreshold = new B2BOrderThresholdPermissionModel();
		lowThreshold.setCurrency(usdCurrency);
		lowThreshold.setThreshold(Double.valueOf(100D));

		final B2BOrderThresholdPermissionModel highThreshold = new B2BOrderThresholdPermissionModel();
		highThreshold.setCurrency(usdCurrency);
		highThreshold.setThreshold(Double.valueOf(1000D));

		final Set<B2BOrderThresholdPermissionModel> permissionModels = new HashSet<>(2);
		permissionModels.add(highThreshold);
		permissionModels.add(lowThreshold);
		final AbstractOrderModel mockAbstractOrderModel = mock(AbstractOrderModel.class);
		when(mockAbstractOrderModel.getCurrency()).thenReturn(usdCurrency);

		// this should return a permission with higher threshold
		Assert.assertEquals(highThreshold,
				defaultB2BOrderThresholdEvaluationStrategy.getPermissionToEvaluate(permissionModels, mockAbstractOrderModel));

	}
}
