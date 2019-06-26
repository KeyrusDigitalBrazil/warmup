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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

@UnitTest
public class XmlMonitoredRequestErrorParserUnitTest
{
	private static final String ERROR_CODE = "Trix";
	private static final String ERROR_MSG = "trix are for kids";
	private static final String UNKNOWN_ERROR_CODE = "unknown_error";

	private final XmlMonitoredRequestErrorParser<MonitoredRequestErrorModel> errorParser =
			new XmlMonitoredRequestErrorParser<>();

	@Test
	public void testIsApplicableForCreatedStatus()
	{
		assertThat(errorParser.isApplicable(MediaType.APPLICATION_XML_VALUE, 201)).isFalse();
	}

	@Test
	public void testIsApplicableForXmlErrorResponse()
	{
		assertThat(errorParser.isApplicable(MediaType.APPLICATION_XML_VALUE, 400)).isTrue();
	}

	@Test
	public void testIsApplicableForXmlUtf8()
	{
		assertThat(errorParser.isApplicable(MediaType.APPLICATION_XML_VALUE+";charset=UTF-8", 500)).isTrue();
	}

	@Test
	public void testIsNotApplicableForJsonResponse()
	{
		assertThat(errorParser.isApplicable(MediaType.APPLICATION_JSON_VALUE, 400)).isFalse();
	}

	@Test
	public void testParseError()
	{
		final String responseBody = errorResponsePayload(ERROR_CODE, ERROR_MSG);

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(ERROR_CODE);
		assertThat(errorModel.getMessage()).isEqualTo(ERROR_MSG);
	}

	@Test
	public void testNoErrorFoundInXml()
	{
		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400,
				"<?xml version='1.0' encoding='utf-8'?>");

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(UNKNOWN_ERROR_CODE);
		assertThat(errorModel.getMessage()).contains("log");
	}

	@Test
	public void testMalformedXmlResponse()
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
		final String responseBody = errorResponsePayload(StringUtils.EMPTY, message);

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(UNKNOWN_ERROR_CODE);
		assertThat(errorModel.getMessage()).isEqualTo(message);
	}

	@Test
	public void testErrorMessageIsEmpty()
	{
		final String code = "my_error_code";
		final String responseBody = errorResponsePayload(code, StringUtils.EMPTY);

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull();
		assertThat(errorModel.getCode()).isEqualTo(code);
		assertThat(errorModel.getMessage()).contains("log");
	}

	@Test
	public void testErrorNull()
	{
		final String code = "my_error_code";
		final String responseBody = errorResponsePayload(code, StringUtils.EMPTY);

		assertThatThrownBy(() -> errorParser.parseErrorFrom(null, 400, responseBody))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Error cannot be null");
	}

	@Test
	public void testParseError_errorMessage()
	{
		final String responseBody = errorResponsePayload(StringUtils.repeat("=", 1000), StringUtils.repeat("=", 1000));

		final MonitoredRequestErrorModel errorModel = errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 400, responseBody);

		assertThat(errorModel).isNotNull()
								.hasFieldOrPropertyWithValue("code", StringUtils.repeat("=", 255 - 3)+"...")
								.hasFieldOrPropertyWithValue("message", StringUtils.repeat("=", 255 - 3)+"...");
	}

	private static String errorResponsePayload(final String errorCode, final String errorMsg)
	{
		return "<?xml version='1.0' encoding='utf-8'?>\n" +
				"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"https://localhost:9002/odataweb/odata2/Cereal/\">\n" +
				"    <content type=\"application/xml\">\n" +
				"        <m:error>\n" +
				"            <d:code>" + errorCode + "</d:code>\n" +
				"            <d:message>" + errorMsg + "</d:message>\n" +
				"        </m:error>\n" +
				"    </content>\n" +
				"</entry>";
	}
}
