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
package de.hybris.platform.sap.productconfig.frontend.beforeview;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class ProductDetailsBeforeViewHandlerTest
{

	private ProductDetailsBeforeViewHandler classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductDetailsBeforeViewHandler();
		classUnderTest.setExtender(new ProductConfigDefaultBeforeViewExtender());
	}

	@Test
	public void testBeforeViewMatch()
	{
		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		classUnderTest.beforeView(null, response, null, ProductDetailsBeforeViewHandler.PRODUCT_CONFIG_PAGE);
		Mockito.verify(response).setHeader("Cache-control", "no-cache, no-store");
		Mockito.verify(response).setHeader("Pragma", "no-cache");
		Mockito.verify(response).setHeader("Expires", "-1");
	}

	@Test
	public void testBeforeViewNoMatch()
	{
		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		classUnderTest.beforeView(null, response, null, "anotherPage");
		Mockito.verifyZeroInteractions(response);
	}
}
