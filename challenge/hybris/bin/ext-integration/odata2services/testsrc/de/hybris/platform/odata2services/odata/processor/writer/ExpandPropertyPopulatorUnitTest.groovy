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
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ExpandPropertyPopulatorUnitTest extends Specification
{
	def expandPopulator = new ExpandPropertyPopulator()
	def request = Mock(ItemLookupRequest)

	@Test
	@Unroll
	def "is applicable when expand property is #expand in request"()
	{
		given:
		request.expand >> expand

		expect:
		expandPopulator.isApplicable(request) == expected

		where:
		expand                              | expected
		null                                | false
		[]                                  | false
		[Mock(NavigationPropertySegment)]   | true
		[[Mock(NavigationPropertySegment)]] | true
	}

	@Test
	def "expand unit property"()
	{
		given:
		def unitNavProperty = navigationProperty("unit", "Units")
		request.expand >> [[unitNavProperty]]

		when:
		def properties = expandPopulator.populate(Mock(EntityProviderWriteProperties), request, Mock(ItemLookupResult)).build()

		then:
		properties.expandSelectTree.links.containsKey("unit")
		properties.expandSelectTree.links.get("unit").isAll()
		!properties.callbacks.isEmpty()
		properties.callbacks.containsKey("unit")
	}

	@Test
	def "expand unit and catalogVersion properties"()
	{
		given:
		def unitNavProperty = navigationProperty("unit", "Units")
		def catalogVersionNavProperty = navigationProperty("catalogVersion", "CatalogVersions")
		request.expand >> [[unitNavProperty], [catalogVersionNavProperty]]

		when:
		def properties = expandPopulator.populate(Mock(EntityProviderWriteProperties), request, Mock(ItemLookupResult)).build()

		then:
		properties.expandSelectTree.links.containsKey("unit")
		properties.expandSelectTree.links.containsKey("catalogVersion")

		properties.expandSelectTree.links.get("unit").isAll()
		properties.expandSelectTree.links.get("catalogVersion").isAll()

		properties.callbacks.containsKey("unit")
		properties.callbacks.containsKey("catalogVersion")
	}
	
	def navigationProperty(final String name, final String entitySet) {
		Mock(NavigationPropertySegment) {
			getNavigationProperty() >>
					Mock(EdmNavigationProperty) { getName() >> name }
			getTargetEntitySet() >>
					Mock(EdmEntitySet) { getName() >> entitySet }
		}
	}
}
