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
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionParameterImplTest;
import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionParametersTest;
import de.hybris.platform.sap.core.jco.connection.impl.JCoExceptionSpliterTest;
import de.hybris.platform.sap.core.jco.test.RFCDirectoryDestinationDataProviderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ JCoConnectionParameterImplTest.class,
        JCoConnectionParametersTest.class, JCoExceptionSpliterTest.class,
        RFCDirectoryDestinationDataProviderTest.class })
public class JCoConnectionTestSuite {
    JCoConnectionTestSuite() {

    }
}
