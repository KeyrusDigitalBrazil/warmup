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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class BackendConfigurationExceptionTest extends TestCase
{
	private BackendConfigurationException classUnderTest;
	final private static String MESSAGE = "myMessage";

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new BackendConfigurationException(MESSAGE);
	}


	public void testGetMessage()
	{
		assertEquals(MESSAGE, classUnderTest.getMessage());
	}

}
