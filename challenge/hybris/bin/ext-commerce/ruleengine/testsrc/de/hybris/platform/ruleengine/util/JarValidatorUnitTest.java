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
package de.hybris.platform.ruleengine.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.zip.UnsupportedZipEntryException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;


@UnitTest
public class JarValidatorUnitTest
{
	private static final String INVALID_ZIP_FILE_PATH = "/ruleengine/test/zipSlipVulnerableSample.zip";
	private static final String VALID_ZIP_FILE_PATH = "/ruleengine/test/zipSlipSafeSample.zip";

	private InputStream getFileInputStream(final String resourcePath) throws FileNotFoundException, URISyntaxException
	{
		final URL url = this.getClass().getResource(resourcePath);
		if (url == null)
		{
			throw new IllegalArgumentException("Resource not found.");
		}
		return new FileInputStream(new File(url.toURI()));
	}

	@Test(expected = UnsupportedZipEntryException.class)
	public void testIsZipSlipInsecure() throws IOException, URISyntaxException
	{
		JarValidator.validateZipSlipSecure(getFileInputStream(INVALID_ZIP_FILE_PATH));
	}

	@Test
	public void testIsZipSlipSecure() throws IOException, URISyntaxException
	{
		JarValidator.validateZipSlipSecure(getFileInputStream(VALID_ZIP_FILE_PATH));
	}
}
