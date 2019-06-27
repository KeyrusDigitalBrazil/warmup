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
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static org.assertj.core.api.Assertions.assertThatThrownBy

@UnitTest
class PartOfGeneratorUnitTest extends Specification {
    TypeAttributeDescriptor attributeDescriptor = Mock(TypeAttributeDescriptor)
	PartOfGenerator generator = Spy(PartOfGenerator) {
        toDescriptor(_) >> attributeDescriptor
    }

	@Test
	def "not applicable to null attribute definition"()
	{
		expect:
		! generator.isApplicable(null)
	}

	@Test
    @Unroll
	def "#result applicable to attribute definition with partOf set to #partOf"() {
        given:
        attributeDescriptor.isPartOf() >> partOf

        expect:
		generator.isApplicable(attributeModel()) == partOf

        where:
        partOf | result
        true   | 'is'
        false  | 'not'
	}

    @Test
	def "generates s:IsPartOf annotation attribute"() {
        given:
		def annotation = generator.generate attributeModel()

        expect:
		annotation.name == 's:IsPartOf'
        annotation.text == 'true'
	}

	@Test
	def "generated annotation attribute is not mutable"() {
        setup:
        final AnnotationAttribute annotation = generator.generate attributeModel()

        expect:
		assertThatThrownBy({annotation.name="OtherName"}).isInstanceOf UnsupportedOperationException
		assertThatThrownBy({annotation.text="Other Value"}).isInstanceOf UnsupportedOperationException
	}

    private IntegrationObjectItemAttributeModel attributeModel() {
        Mock(IntegrationObjectItemAttributeModel)
    }
}
