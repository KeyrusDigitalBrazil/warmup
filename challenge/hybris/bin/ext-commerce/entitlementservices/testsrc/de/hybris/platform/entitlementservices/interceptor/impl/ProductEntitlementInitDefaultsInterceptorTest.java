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
package de.hybris.platform.entitlementservices.interceptor.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;

@IntegrationTest
public class ProductEntitlementInitDefaultsInterceptorTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;

	@Test
	public void shouldApplyDefaultsForEndDate()
	{
		final ProductEntitlementModel model = new ProductEntitlementModel();
		modelService.initDefaults(model);
		assertEquals(EntitlementTimeUnit.MONTH, model.getTimeUnit());
		assertEquals(1, (int) model.getTimeUnitStart());
		assertNull(model.getTimeUnitDuration());
	}
}
