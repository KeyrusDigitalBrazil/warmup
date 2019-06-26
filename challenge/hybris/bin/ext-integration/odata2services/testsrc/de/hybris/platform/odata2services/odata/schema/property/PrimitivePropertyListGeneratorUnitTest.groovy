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
package de.hybris.platform.odata2services.odata.schema.property

import com.google.common.collect.Sets
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.enums.RelationEndCardinalityEnum
import de.hybris.platform.core.model.type.AtomicTypeModel
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import org.apache.olingo.odata2.api.edm.provider.Property
import org.junit.Test

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.complexRelationAttributeBuilder
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder

@UnitTest
class PrimitivePropertyListGeneratorUnitTest extends BasePropertyListGeneratorUnitTest
{
	def keyPropertyGenerator = Mock(SchemaElementGenerator)
	def integrationKeyEdmProperty = Mock(Property)

	def setup()
	{
		propertyListGenerator = new PrimitivePropertyListGenerator()
		propertyListGenerator.setPropertyGenerator(propertyGenerator)
		propertyListGenerator.setIntegrationKeyPropertyGenerator(keyPropertyGenerator)
	}

	@Test
	def "testGenerateEmptyProperties"()
	{
		given:
		def itemModel = mockIntegrationObjectItemModel((Sets.newHashSet()))
		keyPropertyGenerator.generate(itemModel) >> Optional.empty()

		when:
		final List<Property> properties = propertyListGenerator.generate(itemModel)

		then:
		properties.isEmpty()
	}

	@Test
	def "testGenerateNullItem"()
	{
		when:
		propertyListGenerator.generate(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	def "testGenerateListOfProperties"()
	{
		given:
		def unitAttributeModel = simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor()
					.withQualifier("unit")
					.withLocalized(true)
					.withTypeCode("java.lang.String")
					.withType(AtomicTypeModel)
					.withPrimitive(true))
				.withName("unit")
				.build()
		def unitAttributeProperty = Mock(Property)
		def quantityAttributeModel = simpleAttributeBuilder()
				.withAttributeDescriptor(attributeDescriptor()
					.withQualifier("quantity")
					.withLocalized(false)
					.withTypeCode("java.lang.Integer")
					.withType(AtomicTypeModel)
					.withPrimitive(true))
				.withName("quantity")
				.build()
		def quantityAttributeProperty = Mock(Property)
		def complexAttributeModel =
				complexRelationAttributeBuilder()
						.withSource("source")
						.withTarget("target")
						.withSourceCardinality(RelationEndCardinalityEnum.ONE)
						.withTargetCardinality(RelationEndCardinalityEnum.ONE)
						.build()

		propertyGenerator.generate(unitAttributeModel) >> unitAttributeProperty
		propertyGenerator.generate(quantityAttributeModel) >> quantityAttributeProperty
		
		def itemModel = mockIntegrationObjectItemModel(Sets.newHashSet(unitAttributeModel, quantityAttributeModel, complexAttributeModel))
		keyPropertyGenerator.generate(itemModel) >> Optional.of(integrationKeyEdmProperty)

		when:
		final List<Property> properties = propertyListGenerator.generate(itemModel)

		then:
		properties.size() == 3
		properties.containsAll(unitAttributeProperty, quantityAttributeProperty, integrationKeyEdmProperty)
	}
}
