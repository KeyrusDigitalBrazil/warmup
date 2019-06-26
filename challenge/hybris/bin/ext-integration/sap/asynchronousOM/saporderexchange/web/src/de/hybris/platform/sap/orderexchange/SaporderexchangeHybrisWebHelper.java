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
package de.hybris.platform.sap.orderexchange;

import org.apache.log4j.Logger;


/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
@SuppressWarnings("javadoc")
public class SaporderexchangeHybrisWebHelper
{

	private SaporderexchangeHybrisWebHelper() {
		throw new IllegalStateException("Utility class");
	}

	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SaporderexchangeHybrisWebHelper.class.getName());

	public static final String getTestOutput()
	{
		return "testoutput";
	}
}
