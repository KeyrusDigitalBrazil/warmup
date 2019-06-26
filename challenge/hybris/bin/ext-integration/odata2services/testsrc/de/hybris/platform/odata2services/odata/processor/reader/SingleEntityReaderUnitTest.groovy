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
import de.hybris.platform.odata2services.odata.processor.ExpandedEntity
import de.hybris.platform.odata2services.odata.processor.NavigationSegmentExplorer
import de.hybris.platform.odata2services.odata.processor.reader.SingleEntityReader
import de.hybris.platform.odata2services.odata.processor.writer.ResponseWriter
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmMultiplicity
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.apache.olingo.odata2.api.uri.KeyPredicate
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.apache.olingo.odata2.api.uri.UriInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class SingleEntityReaderUnitTest extends Specification
{
	def persistenceService = Mock(PersistenceService)
	def navigationSegmentExplorer = Mock(NavigationSegmentExplorer)
	def responseWriter = Mock(ResponseWriter)

	def entityReader = new SingleEntityReader()

	def setup()
	{
		entityReader.setNavigationSegmentExplorer(navigationSegmentExplorer)
		entityReader.setPersistenceService(persistenceService)
		entityReader.setResponseWriter(responseWriter)
	}

	@Test
	def "isApplicable is true when key predicate exists and no navigation segments"()
	{
		def uriInfo = Mock(UriInfo) {
			getKeyPredicates() >> [Mock(KeyPredicate)]
			getNavigationSegments() >> []
		}

		expect:
		entityReader.isApplicable(uriInfo)
	}

	@Test
	@Unroll
	def "isApplicable is #isApplicable when key predicate is #keyPredicates, navigation segment's multiplicity is #multiplicity"()
	{
		def navigationProperty = Mock(EdmNavigationProperty) {
			getMultiplicity() >> multiplicity
		}
		def navigationSegment = Mock(NavigationSegment) {
			getNavigationProperty() >> navigationProperty
		}
		def uriInfo = Mock(UriInfo) {
			getKeyPredicates() >> keyPredicates
			getNavigationSegments() >> [navigationSegment]
		}

		expect:
		isApplicable == entityReader.isApplicable(uriInfo)

		where:
		keyPredicates        | multiplicity                | isApplicable
		[Mock(KeyPredicate)] | EdmMultiplicity.ZERO_TO_ONE | true
		[Mock(KeyPredicate)] | EdmMultiplicity.ONE         | true
		[Mock(KeyPredicate)] | EdmMultiplicity.MANY        | false
		[]                   | EdmMultiplicity.ONE         | false
	}

	@Test
	@Unroll
	def "isApplicable throws IllegalArgumentException when key predicate is #keyPredicates, and navigation segment is #navigationSegments "()
	{
		def uriInfo = Mock(UriInfo) {
			getKeyPredicates() >> keyPredicates
			getNavigationSegments() >> navigationSegments
		}

		when:
		entityReader.isApplicable(uriInfo)

		then:
		thrown(IllegalArgumentException)

		where:
		keyPredicates        | navigationSegments
		null                 | [Mock(NavigationSegment)]
		[Mock(KeyPredicate)] | null
	}

	@Test
	def "read successfully"()
	{
		def oDataResponse = Mock(ODataResponse)
		def itemLookupRequest = Mock(ItemLookupRequest) {
			getNavigationSegments() >> [Mock(NavigationSegment)]
			getContentType() >> "application/json"
			getExpand() >> Collections.emptyList()
		}
		def properties = Mock(Map)
		def oDataEntry = Mock(ODataEntry) {
			getProperties() >> properties
		}
		def entitySet = Mock(EdmEntitySet)
		def expandedEntity = Mock(ExpandedEntity) {
			getODataEntry() >> oDataEntry
			getEdmEntitySet() >> entitySet
		}

		when:
		def actualODataResponse = entityReader.read(itemLookupRequest)

		then:
		oDataResponse == actualODataResponse
		1 * persistenceService.getEntityData(itemLookupRequest, _ as ConversionOptions) >> oDataEntry
		1 * navigationSegmentExplorer.expandForSingleEntity(itemLookupRequest, oDataEntry) >> expandedEntity
		1 * responseWriter.write(itemLookupRequest, entitySet, properties) >> oDataResponse
	}
}
