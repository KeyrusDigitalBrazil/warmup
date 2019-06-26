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
import de.hybris.platform.odata2services.TestConstants
import org.apache.olingo.odata2.api.edm.EdmMultiplicity
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder

@UnitTest
class LocalizedAttributeAssociationListGeneratorUnitTest extends Specification
{
	def static final NAME_ATTRIBUTE = "name"

	def generator = new LocalizedAttributeAssociationListGenerator()

	@Test
	@Unroll
	def "#numAssociations association(s) are generated when integration object items are #intObjCodeLocalizedMap"()
	{
		given:
		def itemModels = items(intObjCodeLocalizedMap)

		when:
		def associations = generator.generate(itemModels)

		then:
		assertAssociations(associations, intObjCodeLocalizedMap, numAssociations)

		where:
		intObjCodeLocalizedMap 								| numAssociations
		[IntegrationObject1:true]							| 1
		[IntegrationObject1:false]							| 0
		[IntegrationObject1:true, IntegrationObject2:true]	| 2
		[IntegrationObject1:true, IntegrationObject2:false]	| 1
	}

	@Test
	def "No association is generated when the integration object item collection is empty"()
	{
		expect:
		generator.generate([]).isEmpty()
	}

	@Test
	def "exception is thrown when the integration object item collection is null"()
	{
		when:
		generator.generate(null)

		then:
		thrown IllegalArgumentException
	}

	def items(def codeAndLocalizedMap)
	{
		codeAndLocalizedMap.collect {code, localized -> item(code, localized)}
	}

	def item(def code, def localized)
	{
		itemModelBuilder()
			.withCode(code)
			.withAttribute(localizedAttribute(NAME_ATTRIBUTE, localized)).build()
	}

	def localizedAttribute(def attributeName, def localized)
	{
		simpleAttributeBuilder()
			.withAttributeDescriptor(attributeDescriptor().withLocalized(localized))
			.withName(attributeName)
	}

	def assertAssociations(def associations, def codeAndLocalizedMap, def numAssociations)
	{
		def index = 0
		associations.size() == numAssociations
		codeAndLocalizedMap.each
		{ intObjCode, localized ->
			if (localized)
			{
				with(associations.get(index)) {
					getName() == "FK_" + intObjCode + "_localizedAttributes"
					getEnd1().getType().toString() == "HybrisCommerceOData." + intObjCode
					getEnd1().getRole() == intObjCode
					getEnd1().getMultiplicity() == EdmMultiplicity.ONE
					getEnd2().getType().toString() == "HybrisCommerceOData." + TestConstants.LOCALIZED_ENTITY_PREFIX + intObjCode
					getEnd2().getRole() == TestConstants.LOCALIZED_ENTITY_PREFIX + intObjCode
					getEnd2().getMultiplicity() == EdmMultiplicity.MANY
				}
				index++
			}
		}
	}
}
