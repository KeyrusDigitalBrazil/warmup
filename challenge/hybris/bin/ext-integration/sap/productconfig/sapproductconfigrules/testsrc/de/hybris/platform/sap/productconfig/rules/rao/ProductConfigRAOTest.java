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
package de.hybris.platform.sap.productconfig.rules.rao;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigRAOTest
{

	private ProductConfigRAO classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRAO();
	}


	@Test
	public void testToString()
	{
		classUnderTest.setProductCode("pCode");
		classUnderTest.setConfigId("configId123");
		final CsticRAO csticRao = new CsticRAO();
		csticRao.setCsticName("csticName123");
		csticRao.setConfigId("configId123");
		final CsticValueRAO value1 = new CsticValueRAO();
		value1.setCsticValueName("valueName123");
		final CsticValueRAO value2 = new CsticValueRAO();
		value2.setCsticValueName("valueName456");
		csticRao.setAssignableValues(Collections.singletonList(value1));
		csticRao.setAssignedValues(Collections.singletonList(value2));

		classUnderTest.setCstics(Collections.singletonList(csticRao));

		final String string = classUnderTest.toString();
		assertTrue(string.contains("pCode"));
		assertTrue(string.contains("csticName123"));
		assertTrue(string.contains("valueName123"));
		assertTrue(string.contains("configId123"));
	}
}
