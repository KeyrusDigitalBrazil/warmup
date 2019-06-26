/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.kymaintegrationservices.utils;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;


@UnitTest
public class JsonRetrieverTest
{
	@InjectMocks
	private JsonRetriever jsonRetriever;

	@Test(expected = IOException.class)
	public void testInvalidTargetUrl() throws IOException
	{
		jsonRetriever.urlToJson("https://does.not.exist.com/api-doc");
	}

	@Test
	public void testReadFromBuffer() throws IOException
	{
		final String apiSpec = "{\"swagger\":\"2.0\",\"info\":{\"description\":\"ASM Webservices\",";
		final BufferedReader bufferedReader = new BufferedReader(new StringReader(apiSpec));
		final String retrievedSpec = jsonRetriever.readFromBuffer(bufferedReader);
		assertTrue("retrieved spec is not equal to the expected spec", StringUtils.equals(apiSpec, retrievedSpec));
	}

}
