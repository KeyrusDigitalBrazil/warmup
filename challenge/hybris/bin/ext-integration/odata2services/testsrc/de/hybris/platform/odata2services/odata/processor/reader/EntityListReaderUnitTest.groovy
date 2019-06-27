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
package de.hybris.platform.odata2services.odata.processor.reader

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.PersistenceService
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import de.hybris.platform.odata2services.odata.processor.writer.ResponseWriter
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.apache.olingo.odata2.api.uri.KeyPredicate
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.apache.olingo.odata2.api.uri.UriInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class EntityListReaderUnitTest extends Specification {

	def persistenceService = Mock(PersistenceService)
	def responseWriter = Mock(ResponseWriter)
	def entityListReader = new EntityListReader()

	def setup() {
		entityListReader.setPersistenceService(persistenceService)
		entityListReader.setResponseWriter(responseWriter)
	}

	@Test
	@Unroll
	def "isApplicable when #description"() {
		expect:
		entityListReader.isApplicable(uriInfo) == expected

		where:
		description                            | uriInfo                       | expected
		"UriInfo has key predicates"           | uriInfoWithKeyPredicates()    | false
		"UriInfo does not have key predicates" | uriInfoWithoutKeyPredicates() | true
	}

	@Test
	def "isApplicable throws exception when UriInfo has null key predicates"() {
		when:
		entityListReader.isApplicable(uriInfoWithNullKeyPredicates())
		then:
		thrown(IllegalArgumentException)
	}

	@Test
	def "read serializes empty list for request with no filter result"() {
		Stub(ConversionOptions.ConversionOptionsBuilder) {
			build() >> Stub(ConversionOptions)
		}
		def expectedResponse = Mock(ODataResponse)

		def entitySet = Stub(EdmEntitySet)
		def itemLookupRequest = Stub(ItemLookupRequest) {
			getEntitySet() >> entitySet
			isNoFilterResult() >> true
		}

		when:
		def response = entityListReader.read(itemLookupRequest)

		then:
		response == expectedResponse
		0 * persistenceService.getEntities(itemLookupRequest, _ as ConversionOptions) >> multipleProductsFound()
		1 * responseWriter.write(itemLookupRequest, _ as EdmEntitySet, _ as ItemLookupResult) >> expectedResponse
	}

	@Test
	def "read serializes list of feeds with multiple entries mapped"() {
		GroovyMock(ConversionOptions.ConversionOptionsBuilder) {
			build() >> Mock(ConversionOptions)
		}
		def expectedResponse = Mock(ODataResponse)

		def entitySet = Mock(EdmEntitySet)
		def itemLookupRequest = Mock(ItemLookupRequest) {
			getNavigationSegments() >> [Mock(NavigationSegment)]
			getEntitySet() >> entitySet
			getContentType() >> "application/json"
		}

		when:
		def response = entityListReader.read(itemLookupRequest)

		then:
		response == expectedResponse
		1 * persistenceService.getEntities(itemLookupRequest, _ as ConversionOptions) >> multipleProductsFound()
		1 * responseWriter.write(itemLookupRequest, _ as EdmEntitySet, _ as ItemLookupResult) >> expectedResponse
	}

	UriInfo uriInfoWithNullKeyPredicates() {
		Mock(UriInfo) {
			getKeyPredicates() >> null
		}
	}

	UriInfo uriInfoWithoutKeyPredicates() {
		Mock(UriInfo) {
			getKeyPredicates() >> []
		}
	}

	UriInfo uriInfoWithKeyPredicates() {
		Mock(UriInfo) {
			getKeyPredicates() >> Collections.singletonList(KeyPredicate)
		}
	}

	ItemLookupResult multipleProductsFound() {
		Mock(ItemLookupResult) {
			getEntries() >> [
					Mock(ODataEntry) {
						getProperty("code") >> "product1"
						getProperty("name") >> "the first product"
					},
					Mock(ODataEntry) {
						getProperty("code") >> "product2"
						getProperty("name") >> "the second product"
					}]
		}

	}
}
