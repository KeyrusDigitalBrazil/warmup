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
package de.hybris.platform.odata2services.odata.schema.association

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import org.apache.olingo.odata2.api.edm.provider.Association
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class AssociationListGeneratorRegistryUnitTest extends Specification {
	def registry = new AssociationListGeneratorRegistry()

	@Test
	def "resulting list contains 0 associations from null generator list"() {
		given:
		registry.setAssociationListGenerators(null)

		expect:
		registry.generate(itemsDoNotMatter()).isEmpty()
	}


	@Test
	def "generate with null integration object items throws exception"() {
		given:
		registry.setAssociationListGenerators(null)

		when:
		registry.generate(null)

		then:
		thrown(IllegalArgumentException)
	}

	@Test
	@Unroll
	def "resulting list contains #resultSize navigation properties from #generators.size generators"() {
		given:
		registry.setAssociationListGenerators(generators)

		when:
		def result = registry.generate(itemsDoNotMatter())

		then:
		result.size() == resultSize

		where:
		generators                                                                 | resultSize
		[mockGeneratorWithNumberOfResults(2), mockGeneratorWithNumberOfResults(1)] | 3
		[mockGeneratorWithNumberOfResults(1), mockGeneratorWithNumberOfResults(0)] | 1
		[mockGeneratorWithNumberOfResults(5)]                                      | 5
		[]                                                                         | 0
	}

	def itemsDoNotMatter() {
		[]
	}

	def mockGeneratorWithNumberOfResults(def count) {
		Mock(SchemaElementGenerator) {
			generate(_) >> [Mock(Association)] * count
		}
	}
}
