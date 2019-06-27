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
package de.hybris.platform.sap.yorderfulfillment.suites;


import de.hybris.platform.sap.yorderfulfillment.actions.SetCompletionStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.SetConfirmationStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.UpdateERPOrderStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderCancelRepairJobTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderExchangeRepairJobTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
        {UpdateERPOrderStatusActionTest.class, SetConfirmationStatusActionTest.class, SetCompletionStatusActionTest.class, OrderExchangeRepairJobTest.class, OrderCancelRepairJobTest.class})
public class UnitTestSuite {
    // Intentionally left blank
}
