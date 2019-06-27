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
package de.hybris.platform.odata2services.odata.monitoring.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractionException
import org.apache.commons.lang.StringUtils
import org.apache.olingo.odata2.api.commons.HttpContentType
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class XmlIntegrationKeyExtractorUnitTest extends Specification {

	private static final BAD_REQUEST = HttpStatusCodes.BAD_REQUEST.getStatusCode()
	private static final CREATED = HttpStatusCodes.CREATED.getStatusCode()
	private static final String INTEGRATION_KEY = "integrationKeyReturnedFromOdataEntry"

	def extractor = new XmlIntegrationKeyExtractor()

	@Test
	@Unroll
	def "test extract from #responseDesc "() {
		given:
		def response = responseValue
		def code = responseCode

		expect:
		extractor.extractIntegrationKey(response, code) == extractedValue

		where:
		responseDesc                                  | integrationKey                                              | responseValue                  | responseCode | extractedValue
		"Error response with empty response value"    | StringUtils.EMPTY                                           | errorResponse(integrationKey)  | BAD_REQUEST  | StringUtils.EMPTY
		"Error response with empty integration key"   | "<d:innererror></d:innererror>\n"                           | errorResponse(integrationKey)  | BAD_REQUEST  | StringUtils.EMPTY
		"Error response with integration key"         | "<d:innererror>${INTEGRATION_KEY}</d:innererror>\n"         | errorResponse(integrationKey)  | BAD_REQUEST  | INTEGRATION_KEY
		"Success response with empty response value"  | StringUtils.EMPTY                                           | entityResponse(integrationKey) | CREATED      | StringUtils.EMPTY
		"Success response with empty integration key" | "<d:integrationKey></d:integrationKey>\n"                   | entityResponse(integrationKey) | CREATED      | StringUtils.EMPTY
		"Success response with integration key"       | "<d:integrationKey>${INTEGRATION_KEY}</d:integrationKey>\n" | entityResponse(integrationKey) | CREATED      | INTEGRATION_KEY
	}

	@Test
	def "exception is thrown when extract from malformed response body"() {
		given:
		def response = "<not-xml>"
		def code = BAD_REQUEST

		when:
		extractor.extractIntegrationKey(response, code)

		then:
		thrown(IntegrationKeyExtractionException)
	}

	@Test
	@Unroll
	def "test isApplicable when response is #responseType"() {
		expect:
		extractor.isApplicable(responseType) == isApplicable

		where:
		responseType                              | isApplicable
		null                                      | false
		HttpContentType.APPLICATION_JSON          | false
		HttpContentType.APPLICATION_XML_UTF8      | true
		HttpContentType.APPLICATION_XML           | true
		HttpContentType.APPLICATION_ATOM_XML_UTF8 | true
		HttpContentType.APPLICATION_ATOM_XML      | true

	}


	def entityResponse(integrationKeyElement) {
		"<?xml version='1.0' encoding='utf-8'?>\n" +
				"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"https://localhost:9002/odata2webservices/Cereal/\">\n" +
				"    <content type=\"application/xml\">\n" +
				"        <m:properties>\n" +
				"           <d:code>trix</d:code>\n" +
				"           <d:name>Trix</d:name>\n" +
				integrationKeyElement +
				"        </m:properties>\n" +
				"    </content>\n" +
				"</entry>"
	}

	def errorResponse(integrationKeyElement) {
		"<?xml version='1.0' encoding='utf-8'?>\n" +
				"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"https://localhost:9002/odataweb/odata2/Cereal/\">\n" +
				"    <content type=\"application/xml\">\n" +
				"        <m:error>\n" +
				"			<d:code> errorCode </d:code>\n" +
				"			<d:message> errorMsg </d:message>\n" +
				integrationKeyElement +
				"        </m:error>\n" +
				"    </content>\n" +
				"</entry>";
	}
}