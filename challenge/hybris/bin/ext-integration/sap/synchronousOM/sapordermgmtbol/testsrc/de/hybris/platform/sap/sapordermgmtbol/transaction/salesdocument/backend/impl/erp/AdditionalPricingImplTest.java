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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class AdditionalPricingImplTest extends SapordermanagmentBolSpringJunitTest
{
	AdditionalPricingImpl classUnderTest = null;


	@Before
	public void init()
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ADDITIONAL_PRICING);
	}

	@Test
	public void testInstance()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testPriceType()
	{
		final String priceType = classUnderTest.getPriceType();
		assertNotNull(priceType);
		assertEquals("H", priceType);
	}

	@Test
	public void testCartCall()
	{
		assertTrue(classUnderTest.isPricingCallCart());
	}

	@Test
	public void testOrderCall()
	{
		assertFalse(classUnderTest.isPricingCallOrder());
	}
}
