/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationCopyStrategyImplTest
{
	@InjectMocks
	private DefaultConfigurationCopyStrategyImpl classUnderTest;

	@Mock
	private ConfigurationDeepCopyHandler configDeepCopyHandler;
	@Mock
	private AbstractOrderModel target;
	@Mock
	private AbstractOrderModel source;

	@Test
	public void testDeepCopyConfiguration()
	{
		final String newConfigId = classUnderTest.deepCopyConfiguration("configId", "productCode", "extConfig", true);
		verify(configDeepCopyHandler, times(1)).deepCopyConfiguration(eq("configId"), eq("productCode"), eq("extConfig"),
				anyBoolean(), (ProductConfigurationRelatedObjectType) isNull());
	}

	@Test
	public void testFinalizeClone()
	{
		classUnderTest.finalizeClone(source, target);
		verify(configDeepCopyHandler, times(0)).deepCopyConfiguration(anyString(), anyString(), any(), anyBoolean(),
				(ProductConfigurationRelatedObjectType) isNull());
	}
}
