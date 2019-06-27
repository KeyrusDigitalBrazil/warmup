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
package de.hybris.platform.sap.core.jco.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.monitor.jaxb.JCoMonitorJAXBHandlerTest;
import de.hybris.platform.sap.core.jco.monitor.provider.JCoConnectionMonitorClusterProviderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Spring testsuite for extension sapcorejco.
 */
@UnitTest
@RunWith(Suite.class)
@SuiteClasses(
{ JCoMonitorJAXBHandlerTest.class, JCoConnectionMonitorClusterProviderTest.class, RFCDirectoryDestinationDataProviderTest.class })
public class SapcorejcoSpringTestSuite
{

}
