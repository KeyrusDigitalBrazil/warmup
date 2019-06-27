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
package de.hybris.platform.mobileservices.text;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{
		ActivationOfAggregatorsAndShortcodesTest.class, //
		ActivationTest.class, //
		AliasesTest.class, //
		DedicatedShortcodeTest.class, //
		FailureRecoveryTest.class, //
		RemoveInterceptorValidationTest.class, //
		SharedShortcodeTest.class, //
		StatusRecordTestBase.class, //
		UseCaseMTBS01FreeTextTest.class, //
		UseCaseMTBS02to08ItemLinksTest.class, //
		UseCaseMTBS10TestSorryMessageTest.class, //
		UseCaseMTBS11Test2WaysTest.class, //
		UseCaseMTBS12Test1WayTest.class })
public class StatusRecordTestSuite
{
	//
}
