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
package de.hybris.platform.sap.sapordermgmtbol.unittests;

import junit.framework.Test;
import junit.framework.TestSuite;


@SuppressWarnings("javadoc")
public class UnitTestSuite
{

	public static Test suite()
	{
		final TestSuite suite = new TestSuite("Salestransactions: Unit Tests");
		suite.addTest(beERPSuite());
		return suite;
	}



	private static Test beERPSuite()
	{
		final TestSuite suite = new TestSuite("BE ERP Layer");

		return suite;
	}

}
