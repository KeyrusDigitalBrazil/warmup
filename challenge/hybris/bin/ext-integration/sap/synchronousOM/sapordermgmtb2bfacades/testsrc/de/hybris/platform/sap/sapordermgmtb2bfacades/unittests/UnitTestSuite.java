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
package de.hybris.platform.sap.sapordermgmtb2bfacades.unittests;

import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.DefaultCartRestorationFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.DefaultSapCartFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl.SapOrdermgmtB2BCartFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.checkout.impl.SapOrdermgmtB2BCheckoutFacadeTest;
import de.hybris.platform.sap.sapordermgmtb2bfacades.impl.DefaultProductImageHelperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 *
 */
@SuppressWarnings("squid:S2094")
@RunWith(Suite.class)
@SuiteClasses(
{ SapOrdermgmtB2BCheckoutFacadeTest.class, SapOrdermgmtB2BCartFacadeTest.class, DefaultCartRestorationFacadeTest.class,

		DefaultProductImageHelperTest.class, DefaultSapCartFacadeTest.class })
public class UnitTestSuite
{
	//Left empty
}
