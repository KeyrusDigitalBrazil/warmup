/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/
package de.hybris.platform.yaasconfiguration;

import static org.hamcrest.Matchers.containsString;

import org.junit.Rule;
import org.junit.rules.ExpectedException;


public class YaasConfigurationTestUtils
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	public void errorMustBeReported(final String msg)
	{
		expectedException.expectMessage(containsString(msg));
	}
}
