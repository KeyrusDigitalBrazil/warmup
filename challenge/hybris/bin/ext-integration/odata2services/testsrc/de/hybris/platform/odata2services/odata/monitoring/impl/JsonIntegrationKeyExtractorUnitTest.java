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
 */
package de.hybris.platform.odata2services.odata.monitoring.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractor;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jayway.jsonpath.JsonPath;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonPath.class)
@PowerMockIgnore({ "org.apache.logging.log4j.spi.Provider", "javax.management.*" })
public class JsonIntegrationKeyExtractorUnitTest
{
	private final IntegrationKeyExtractor extractor = new JsonIntegrationKeyExtractor();
	private static final String INTEGRATION_KEY = "integrationKeyReturnedFromOdataEntry";

	@Test
	public void testIsApplicableWhenResponseIsNull()
	{
		assertThat(extractor.isApplicable(null)).isFalse();
	}

	@Test
	public void testIsApplicableWhenResponseIsAcceptJson()
	{
		assertThat(extractor.isApplicable(HttpContentType.APPLICATION_JSON)).isTrue();
	}

	@Test
	public void testIsApplicableWhenResponseIsXmlUtf8()
	{
		assertThat(extractor.isApplicable(HttpContentType.APPLICATION_XML_UTF8)).isFalse();
	}

	@Test
	public void testExtractIntegrationKeyForErrorResponse()
	{
		assertThat(
				extractor.extractIntegrationKey(
						errorResponseInputStream("\"innererror\": \"" + INTEGRATION_KEY + "\",\n"), HttpStatusCodes.BAD_REQUEST.getStatusCode()
				)
		).isEqualTo(INTEGRATION_KEY);
	}

	@Test
	public void testExtractIntegrationKeyForSuccessResponse()
	{
		assertThat(
				extractor.extractIntegrationKey(
						responseInputStream("\"integrationKey\": \"" + INTEGRATION_KEY + "\",\n"), HttpStatusCodes.CREATED.getStatusCode()
				)
		).isEqualTo(INTEGRATION_KEY);
	}

	@Test
	public void testExtractIntegrationKeyWhenErrorResponseMissingKey()
	{
		assertThat(
				extractor.extractIntegrationKey(
						errorResponseInputStream(StringUtils.EMPTY), HttpStatusCodes.BAD_REQUEST.getStatusCode()
				)
		).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	public void testExtractIntegrationKeyWhenSuccessResponseMissingKey()
	{
		assertThat(
				extractor.extractIntegrationKey(
						responseInputStream(StringUtils.EMPTY), HttpStatusCodes.CREATED.getStatusCode()
				)
		).isEqualTo(StringUtils.EMPTY);
	}

	private static String responseInputStream(final String integrationKeyElement)
	{
		return  "{\n" +
				"    \"d\": {\n" +
						integrationKeyElement +
				"		\"name\": \"Trix\",\n" +
				"		\"description\": Trix are for kids,\n" +
				"		\"code\": \"trix\"" +
				"    }\n" +
				"}";
	}

	private static String errorResponseInputStream(final String integrationKeyElement)
	{
		return	"{\n" +
				"	\"error\": {\n" +
						integrationKeyElement +
				"		\"code\": \" codeValue\",\n" +
				"		\"message\": {\n" +
				"			\"lang\": \"en\",\n" +
				"			\"value\": \"message\"\n" +
				"		}\n" +
				"	}\n" +
				"}";
	}
}