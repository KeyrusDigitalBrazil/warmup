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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultProductLinkStrategyImplTest
{
	private static final String PRODUCT_CODE = "product code";
	private static final String CONFIG_ID = "config id";

	private DefaultProductLinkStrategyImpl classUnderTest;

	@Mock
	private SessionAccessService sessionAccessService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultProductLinkStrategyImpl();
		classUnderTest.setSessionAccessService(sessionAccessService);
	}


	@Test
	public void testGetConfigIdForProduct()
	{
		classUnderTest.getConfigIdForProduct(PRODUCT_CODE);
		Mockito.verify(sessionAccessService).getConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testSetConfigIdForProduct()
	{
		classUnderTest.setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
		Mockito.verify(sessionAccessService).setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
	}

	@Test
	public void testRemoveConfigIdForProduct()
	{
		classUnderTest.removeConfigIdForProduct(PRODUCT_CODE);
		Mockito.verify(sessionAccessService).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testRetrieveProductCode()
	{
		classUnderTest.retrieveProductCode(CONFIG_ID);
		Mockito.verify(sessionAccessService).getProductForConfigId(CONFIG_ID);
	}
}
