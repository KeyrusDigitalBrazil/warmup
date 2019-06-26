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
package de.hybris.platform.marketplaceservices.vendor.impl;

import static org.junit.Assert.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultVendorPromotionRuleEngineContextServiceTest
{
	private final String contextName = "default";

	private DefaultVendorPromotionRuleEngineContextService vendorPromotionRuleEngineContextService;

	@Mock
	private AbstractRuleEngineContextModel context;

	@Mock
	private RuleEngineContextDao ruleEngineContextDao;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		vendorPromotionRuleEngineContextService = new DefaultVendorPromotionRuleEngineContextService();
		vendorPromotionRuleEngineContextService.setRuleEngineContextDao(ruleEngineContextDao);

	}

	@Test
	public void testfindVendorRuleEngineContextByName()
	{
		Mockito.doReturn(context).when(ruleEngineContextDao).findRuleEngineContextByName(Mockito.any());
		final AbstractRuleEngineContextModel returnContext = vendorPromotionRuleEngineContextService
				.findVendorRuleEngineContextByName(contextName);
		assertEquals(context, returnContext);
	}

}
