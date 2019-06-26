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
package de.hybris.platform.odata2services.odata.processor.writer

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultResponseWriterUnitTest extends Specification {
	def responseWriter = Spy(DefaultResponseWriter)

	def populator = Mock(ResponseWriterPropertyPopulator)
	def populatorRegistry = Mock(ResponseWriterPropertyPopulatorRegistry) {
		getPopulators(_ as ItemLookupRequest) >> [populator]
	}

	def setup() {
		responseWriter.setPopulatorRegistry(populatorRegistry)
	}

	@Test
	def "writes a single entity"() throws ODataException {
		given:
		populator.populate(_, _, null) >> Mock(EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder)
		def expected = Mock(ODataResponse)
		responseWriter.write(_, _, _, _) >> expected

		expect:
		responseWriter.write(itemLookupRequest(), Mock(EdmEntitySet), Mock(Map)) == expected
	}

	@Test
	def "writes a collection of entities"() throws ODataException {
		given:
		def result = entitySetResult()
		def request = itemLookupRequest()
		result.getTotalCount() >> 5
		def expected = Mock(ODataResponse)
		populator.populate(_ as EntityProviderWriteProperties, request, result) >> EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder.newInstance().inlineCount(5)

		when:
		def actual = responseWriter.write(request, Mock(EdmEntitySet), result)

		then:
		1 * responseWriter.write(_, _, _, _) >> { args ->
			assert args[3].inlineCount == result.totalCount
			expected
		}
		actual == expected
	}

	@Test
	def "writes a collection of entities with unchanged write properties when they are not present in the request"() throws ODataException {
		given:
		def request = itemLookupRequest()
		request.getODataEntry() >> Mock(ODataEntry)
		def result = entitySetResult()
		def expected = Mock(ODataResponse)
		responseWriter.write(_, _, _, _) >> expected

		when:
		def actual = responseWriter.write(request, Mock(EdmEntitySet.class), result)

		then:
		0 * request.isCount()
		actual == expected
	}

	@Test
	def "writes a collection of entities without inlined count when it's not present in the request"() throws ODataException {
		given:
		def result = entitySetResult()
		def expected = Mock(ODataResponse)
		def request = itemLookupRequestWithNoCount()
		populator.populate(_ as EntityProviderWriteProperties, request, result) >> EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder.newInstance()

		when:
		def actual = responseWriter.write(request, Mock(EdmEntitySet.class), result)

		then:
		1 * responseWriter.write(_, _, _, _) >> { args ->
			assert args[3].inlineCount == null
			assert args[3].inlineCountType == null
			expected
		}
		actual == expected
	}

	ItemLookupRequest itemLookupRequestWithNoCount() {
		Mock(ItemLookupRequest) {
			getContentType() >> "application/json"
			isCount() >> false
		}
	}

	ItemLookupRequest itemLookupRequest() {
		Mock(ItemLookupRequest) {
			getContentType() >> "application/json"
			isCount() >> true
			getRequestUri() >> URI.create("http://test")
			getSkip() >> 0
			getTop() >> 100
		}
	}

	ItemLookupResult entitySetResult() {
		def oDataEntry = Mock(ODataEntry) {
			getProperties() >> ["key": "value"]
		}

		Mock(ItemLookupResult) {
			getEntries() >> [oDataEntry]
		}
	}
}