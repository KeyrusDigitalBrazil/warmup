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
package de.hybris.platform.odata2services.odata.schema.entity


import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.junit.Test
import spock.lang.Specification

@UnitTest
class EntityTypeListGeneratorUnitTest extends Specification
{
	def generator1 = Mock(SchemaElementGenerator)
	def generator2 = Mock(SchemaElementGenerator)
	def entityTypeListGenerator = new EntityTypeListGenerator()

	def setup()
	{
		entityTypeListGenerator.setEntityTypeGenerators([generator1, generator2])
	}

	@Test
	def "entity list generator returns unique entity types"()
	{
		def addressEntityType = givenEntityType("address")
		def integerEntityType = givenEntityType("integer")
		def longEntityType = givenEntityType("long")
		generator1.generate(_) >> [addressEntityType, integerEntityType]
		generator2.generate(_) >> [integerEntityType, longEntityType]

		expect:
		def result = entityTypeListGenerator.generate([Stub(IntegrationObjectItemModel)])
		result.size() == 3
		result.containsAll([integerEntityType, addressEntityType, longEntityType])
	}

	@Test
	def "null integrationObjectItemModel collection fails precondition check"()
	{
		when:
		entityTypeListGenerator.generate(null)

		then:
		thrown(IllegalArgumentException)
	}

	def "empty collection results in empty list"()
	{
		when:
		def typeList = entityTypeListGenerator.generate(Collections.emptyList())

		then:
		typeList.size() == 0
	}

	def givenEntityType(def name)
	{
		def entityType = Stub(EntityType)
		entityType.getName() >> name
		return entityType
	}
}
