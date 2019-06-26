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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigRulesResultUtilImplTest
{
	private static final String CONFIG_ID = "configId";
	private ProductConfigRulesResultUtilImpl classUnderTest;


	@Before
	public void setup()
	{
		classUnderTest = new ProductConfigRulesResultUtilImpl();
	}

	@Test
	public void testRetrieveRulesBasedVariantConditionModifications()
	{
		assertTrue(classUnderTest.retrieveRulesBasedVariantConditionModifications(CONFIG_ID).isEmpty());
	}

	@Test
	public void testRetrieveDiscountMessages()
	{
		assertTrue(classUnderTest.retrieveDiscountMessages(CONFIG_ID).isEmpty());
	}


}
