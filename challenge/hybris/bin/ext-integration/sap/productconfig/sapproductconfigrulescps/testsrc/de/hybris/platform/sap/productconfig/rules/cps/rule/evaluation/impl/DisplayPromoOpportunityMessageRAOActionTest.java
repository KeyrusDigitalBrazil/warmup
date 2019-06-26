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
package de.hybris.platform.sap.productconfig.rules.cps.rule.evaluation.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl.AbstractProductConfigRAOActionTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayPromoOpportunityMessageRAOActionTest extends AbstractProductConfigRAOActionTest
{
	private DisplayPromoOpportunityMessageRAOAction action;

	@Before
	public void setUp()
	{
		action = new DisplayPromoOpportunityMessageRAOAction();
	}

	@Test
	public void testDisplayPromoOpportunityMessageGetgetPromoType()
	{
		assertEquals(ProductConfigMessagePromoType.PROMO_OPPORTUNITY, action.getPromoType());
	}
}
