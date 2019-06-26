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
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Test
import spock.lang.Specification

@UnitTest
class NextLinkPropertyPopulatorUnitTest extends Specification {

	def TOTAL_COUNT = 45

	def request = Mock(ItemLookupRequest) {
		getRequestUri() >> URI.create("www.domain.es")
		getSkip() >> 0
		getTop() >> 2
	}

	def populator = new NextLinkPropertyPopulator()

	@Test
	def "isApplicable is true when ODataEntry is null"() {
		expect:
		populator.isApplicable(Mock(ItemLookupRequest))
	}

	@Test
	def "isApplicable is false for GET request of single item"() {
		given:
		request.getODataEntry() >> Mock(ODataEntry)

		expect:
		! populator.isApplicable(request)
	}

	@Test
	def "populate provides builder with next link"() {

		def result = Mock(ItemLookupResult) {
			getTotalCount() >> TOTAL_COUNT
		}

		when:
		def response = populator.populate(Mock(EntityProviderWriteProperties), request, result)

		then:
		response.build().nextLink == "www.domain.es?\$skiptoken=2"
	}

	@Test
	def "populate provides builder with existing properties provided"() {

		def properties = EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder.newInstance().inlineCount(6).build()

		when:
		def response = populator.populate(properties, request, Mock(ItemLookupResult))

		then:
		response.build().inlineCount == 6
	}
}
