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
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public abstract class AbstractGeneratorTest
{
	protected String getResourceAsString(final String name) throws IOException
	{
		try (InputStream inputStream = getClass().getResourceAsStream(name))
		{
			return IOUtils.toString(this.getClass().getResourceAsStream(name), "UTF-8");
		}
	}

	protected String removeAllWhitespaces(final String inputStr)
	{
		return inputStr.replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");
	}
}
