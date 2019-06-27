/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctest

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.textfieldconfiguratortemplateocctest.controllers.ProductTextfieldConfiguratorControllerTest

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RunWith(Suite.class)
@Suite.SuiteClasses([ProductTextfieldConfiguratorControllerTest])
@IntegrationTest
class AllTextFieldConfiguratorSpockTests {

    private static final Logger LOG = LoggerFactory.getLogger(AllTextFieldConfiguratorSpockTests.class)

    @BeforeClass
    static void setUpClass() {
        TestSetupStandalone.loadData()
        TestSetupStandalone.startServer()
    }

    @AfterClass
    static void tearDown() {
        TestSetupStandalone.stopServer()
    }

    @Test
    static void testing() {
        //dummy test class
    }
}
