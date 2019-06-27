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

package de.hybris.platform.integrationservices.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

@UnitTest
public class JsonMonitoredRequestErrorParserUnitTest
{
	private static final String UNKNOWN_ERROR_CODE = "unknown_error";
	private final JsonMonitoredRequestErrorParser<MonitoredRequestErrorModel> errorParser =
			new JsonMonitoredRequestErrorParser<>();

	@Test
	public void testIsApplicableForCreatedStatus()
	{
		assertThat(errorParser.isApplicable(APPLICATION_JSON_VALUE, 201)).isFalse();
	}

	@Test
	public void testIsApplicableForErrorStatus()
	{
		assertThat(errorParser.isApplicable(APPLICATION_JSON_VALUE, 400)).isTrue();
	}

	@Test
	public void testIsApplicableForXmlResponse()
	{
		assertThat(errorParser.isApplicable(APPLICATION_XML_VALUE, 500)).isFalse();
	}

	@Test
	public void testParseError()
	{
		final String code = "some_error_code";
		final String message = "An error occurred during the execution";
		final String responseBody = errorResponseBodyWith(code, message);
		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(code);
		assertThat(errorModel.getMessage()).isEqualTo(message);
	}

	@Test
	public void testNoErrorFoundInJson()
	{
		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, "{}");

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(UNKNOWN_ERROR_CODE);
		assertThat(errorModel.getMessage()).contains("log");
	}

	@Test
	public void testMalformedJsonResponse()
	{
		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, "-*<");

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(UNKNOWN_ERROR_CODE);
		assertThat(errorModel.getMessage()).contains("log");
	}

	@Test
	public void testErrorCodeIsEmpty()
	{
		final String message = "An error occurred during the execution";
		final String responseBody = errorResponseBodyWith(StringUtils.EMPTY, message);

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(UNKNOWN_ERROR_CODE);
		assertThat(errorModel.getMessage()).isEqualTo(message);
	}

	@Test
	public void testErrorMessageIsEmpty()
	{
		final String code = "my_error_code";
		final String responseBody = errorResponseBodyWith(code, StringUtils.EMPTY);

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(code);
		assertThat(errorModel.getMessage()).contains("log");
	}

	@Test
	public void testErrorClassNull()
	{
		final String code = "my_error_code";
		final String responseBody = errorResponseBodyWith(code, StringUtils.EMPTY);
		assertThatThrownBy(() -> errorParser.parseErrorFrom(null, 400, responseBody))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Error cannot be null");
	}

	@Test
	public void testParseError_errorMessage()
	{
		final String responseBody = errorResponseBodyWith(StringUtils.repeat("=", 1000), StringUtils.repeat("=", 1000));

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull()
								.hasFieldOrPropertyWithValue("code", StringUtils.repeat("=", 255 - 3)+"...")
								.hasFieldOrPropertyWithValue("message", StringUtils.repeat("=", 255 - 3)+"...");
	}

	private static String errorResponseBodyWith(final String code, final String message)
	{
		return "{\n" +
				"    \"error\": {\n" +
				"        \"code\": \"" + code + "\",\n" +
				"        \"message\": {\n" +
				"            \"lang\": \"en\",\n" +
				"            \"value\": \"" + message + "\"\n" +
				"        }\n" +
				"    }\n" +
				"}";
	}
}