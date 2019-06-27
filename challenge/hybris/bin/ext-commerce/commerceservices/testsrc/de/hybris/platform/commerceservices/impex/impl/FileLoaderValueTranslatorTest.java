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
package de.hybris.platform.commerceservices.impex.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class FileLoaderValueTranslatorTest extends ServicelayerTransactionalTest
{
	@Test
	public void testJarFileLoad() throws Exception
	{
		final FileLoaderValueTranslator fileLoaderValueTranslator = new FileLoaderValueTranslator();
		final String file = "jar:de.hybris.platform.commerceservices.jalo.CommerceServicesManager&/commerceservices/test/fileLoaderTranslatorTest.impex";
		final String input = fileLoaderValueTranslator.importData(file);

		// Calculate the total expected file length including operating system line terminators
		final int expectedFileLineDataLength = 2081;
		final int expectedFileNumberOfLines = 39;
		final int expectFileLineTerminatorSize = System.getProperty("line.separator").length() * expectedFileNumberOfLines;
		final int expectedFileLength = expectedFileLineDataLength + expectFileLineTerminatorSize;

		Assert.assertEquals("Invalid file length", expectedFileLength, input.length());
	}
}
