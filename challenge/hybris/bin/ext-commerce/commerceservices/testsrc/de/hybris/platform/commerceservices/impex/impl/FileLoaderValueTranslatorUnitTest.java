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

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FileLoaderValueTranslatorUnitTest
{
	private static final String ZIP_FILE_PATH = "/commerceservices/test/fileLoaderTranslatorTest.zip";
	private final FileLoaderValueTranslator fileLoaderValueTranslator = new FileLoaderValueTranslator();

	@Before
	public void setUp()
	{
	}

	private File file(final String resource) throws URISyntaxException
	{
		final URL url = this.getClass().getResource(resource);
		if (url == null)
		{
			throw new IllegalArgumentException("Resource not found.");
		}
		return new File(url.toURI());
	}

	@Test
	public void testLoadFromZipFile() throws URISyntaxException
	{
		final File zipFile = this.file(ZIP_FILE_PATH);

		final String file = String.format("zip:%s&fileLoaderTranslatorTest.impex", zipFile.getAbsolutePath());
		final String input = fileLoaderValueTranslator.importData(file);

		// Calculate the total expected file length including operating system line terminators
		final int expectedFileLineDataLength = 2081;
		final int expectedFileNumberOfLines = 39;
		final int expectFileLineTerminatorSize = System.getProperty("line.separator").length() * expectedFileNumberOfLines;
		final int expectedFileLength = expectedFileLineDataLength + expectFileLineTerminatorSize;

		Assert.assertEquals("Invalid file length", expectedFileLength, input.length());
	}

	@Test
	public void testLoadFromMalformedZipFile() throws URISyntaxException
	{
		final File zipFile = this.file(ZIP_FILE_PATH);

		// the zip archive is malformed - it has an entry with name "../fileLoaderTranslatorTest.impex"
		final String file = String.format("zip:%s&../fileLoaderTranslatorTest.impex", zipFile.getAbsolutePath());
		try
		{
			fileLoaderValueTranslator.importData(file);
			// an attempt to get the entry by the name with traversal directory should fail for the reasons of security
			Assert.assertTrue("Directory traversal for Zip entries is prohibited!", false);
		}
		catch (final IllegalArgumentException e)
		{
			Assert.assertEquals("Unexpected reason of fail",
					"Invalid path definition: Directory traversal for Zip entries is not allowed", e.getMessage());
		}
	}
}
