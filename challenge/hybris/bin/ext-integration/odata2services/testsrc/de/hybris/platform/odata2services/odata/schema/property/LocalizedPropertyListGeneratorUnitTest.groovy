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
import de.hybris.platform.core.model.type.AtomicTypeModel
import org.apache.olingo.odata2.api.edm.provider.Property
import org.junit.Test

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder

@UnitTest
class LocalizedPropertyListGeneratorUnitTest extends BasePropertyListGeneratorUnitTest
{
	def setup()
	{
		propertyListGenerator = new LocalizedPropertyListGenerator()
		propertyListGenerator.setPropertyGenerator(propertyGenerator)
	}

	@Test
	def "generate empty properties"()
	{
		given:
		def itemModel = mockIntegrationObjectItemModel(Sets.newHashSet())

		when:
		final List<Property> properties = propertyListGenerator.generate(itemModel)


		then:
		properties.size() == 1
	}

	@Test
	def "generate null item"()
	{
		when:
		propertyListGenerator.generate(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	def "generate list of properties returns only 1 localized property & localized entity key"()
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
		propertyGenerator.generate(unitAttributeModel) >> unitAttributeProperty

		def itemModel = mockIntegrationObjectItemModel(Sets.newHashSet(unitAttributeModel, quantityAttributeModel))

		when:
		final List<Property> properties = propertyListGenerator.generate(itemModel)

		then:
		properties.size() == 2
		properties.contains(unitAttributeProperty)
	}
}