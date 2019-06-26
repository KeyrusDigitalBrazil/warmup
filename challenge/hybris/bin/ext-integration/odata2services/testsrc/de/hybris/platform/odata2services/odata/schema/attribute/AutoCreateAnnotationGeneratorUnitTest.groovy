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

package de.hybris.platform.odata2services.odata.schema.attribute

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class AutoCreateAnnotationGeneratorUnitTest extends Specification {

	def generator = new AutoCreateAnnotationGenerator()

	@Test
	@Unroll
	def "isApplicable is #applicable for #item"() {
		expect:
		generator.isApplicable(item) == applicable

		where:
		applicable | item
		false      | null
		true       | autoCreateAttribute()
		false      | regularAttribute()
		false      | Mock(IntegrationObjectItemAttributeModel) { getAutoCreate() >> null }
	}

	@Test
	def "generate IsAutoCreate annotation"() {
		when:
		def annotation = generator.generate autoCreateAttribute()

		then:
		annotation.name == "s:IsAutoCreate"
		annotation.text == "true"
	}

	IntegrationObjectItemAttributeModel regularAttribute() {
		Mock(IntegrationObjectItemAttributeModel) {
			getAutoCreate() >> false
		}
	}

	IntegrationObjectItemAttributeModel autoCreateAttribute()
	{
		Mock(IntegrationObjectItemAttributeModel) {
			getAutoCreate() >> true
		}
	}
}
