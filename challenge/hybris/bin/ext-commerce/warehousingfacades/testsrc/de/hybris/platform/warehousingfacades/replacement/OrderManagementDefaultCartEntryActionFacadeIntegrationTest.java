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
package de.hybris.platform.warehousingfacades.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.impl.DefaultCartEntryActionFacadeIntegrationTest;

import org.junit.Assert;
import org.junit.Test;


/**
 * Re-implements test {@link DefaultCartEntryActionFacadeIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = DefaultCartEntryActionFacadeIntegrationTest.class)
public class OrderManagementDefaultCartEntryActionFacadeIntegrationTest extends DefaultCartEntryActionFacadeIntegrationTest
{
	@Override
	@Test
	public void shouldExecuteRemoveAction()
	{
		//TODO need to add rule engine context when add to cart
		Assert.assertTrue(true);
	}
}
