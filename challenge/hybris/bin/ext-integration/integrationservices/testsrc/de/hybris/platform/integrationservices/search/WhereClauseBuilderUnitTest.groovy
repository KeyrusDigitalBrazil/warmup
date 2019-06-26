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

package de.hybris.platform.integrationservices.search

import com.google.common.collect.ImmutableMap
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.enums.RelationEndCardinalityEnum
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.RelationDescriptorModel
import de.hybris.platform.core.model.type.RelationMetaTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static org.assertj.core.api.Assertions.assertThat

@UnitTest
class WhereClauseBuilderUnitTest extends Specification
{
	def builder = WhereClauseBuilder.builder()

	@Test
	def "no where clause created"()
	{
		when:
		def clause = builder.build()

		then:
		clause == ""
	}

	def setup()
	{
		builder.withIntegrationObjectItem(item("someitem"))
	}

	@Test
	def "generates WHERE clause if at least one parameter added"()
	{
		when:
		def query = builder
				.withParameters(["someNumber": 10]).build()

		then:
		query.trim().capitalize().startsWith 'WHERE '
		assertThat(query.substring(6).trim())
				.startsWith("{someitem:someNumber}")
				.contains('=')
				.endsWith("?someNumber")
	}

	@Test
	def "connects WHERE conditions with AND when several parameters added"()
	{
		when:
		def query = builder
				.withParameters(ImmutableMap.of("param1", "one", "param2", "two"))
				.build()

		then:
		query.trim().capitalize().startsWith 'WHERE '
		assertThat(query.substring(6).trim())
				.contains("{someitem:param1}")
				.containsIgnoringCase(" AND ")
				.contains("{someitem:param2}")
	}

	@Test
	def "generates WHERE clause with null values"()
	{
		when:
		def parameters = new HashMap()
		parameters.put("value1", 10)
		parameters.put("value2", null)
		parameters.put("value3", "null")
		def query = builder.withParameters(parameters).build()

		then:
		query.trim().capitalize().startsWith 'WHERE '
		assertThat(query.substring(6).trim())
				.contains("{someitem:value1} = ?value1")
				.containsIgnoringCase(" AND ")
				.contains("{someitem:value2} IS NULL")
				.contains("{someitem:value3} IS NULL")
	}

	@Test
	@Unroll
	def "generates join when filtering on property that has a many-to-many #end relation"()
	{
		given:
		def condition = new WhereClauseCondition('{' + propertyName + '} = 1234')
		def filter = new WhereClauseConditions(condition)
		
		when:
		def query = builder
				.withIntegrationObjectItem(item)
				.withFilter(filter)
				.build()

		then:
		query.contains('{relationname:' +  end + '}')

		where:
		end      | propertyName      | item
		'source' | 'supercategories' | itemWithSourceRelation()
		'target' | 'products'        | itemWithTargetRelation()
	}

	@Test
	def "generates join for multiple filtering conditions"()
	{
		given:
		def manyToManyCondition = new WhereClauseCondition('{supercategories} = 1234', ConjunctiveOperator.AND)
		def oneToOneCondition = new WhereClauseCondition('{code} = mycode')
		def filter = new WhereClauseConditions(manyToManyCondition, oneToOneCondition)

		when:
		def query = builder
				.withIntegrationObjectItem(itemWithSourceRelation())
				.withFilter(filter)
				.build()

		then:
		query == ' WHERE {relationname:source} = 1234 AND {product:code} = mycode'
	}

	def itemWithSourceRelation() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(ComposedTypeModel) {
				def relation = Mock(RelationDescriptorModel) {
					getRelationType() >> Mock(RelationMetaTypeModel) {
						getSourceTypeRole() >> "supercategories"
						getSourceTypeCardinality() >> RelationEndCardinalityEnum.MANY
						getTargetTypeCardinality() >> RelationEndCardinalityEnum.MANY
					}
					getRelationName() >> "RelationName"
				}
				getDeclaredattributedescriptors() >> Collections.singletonList(relation)
				getCode() >> "Product"
			}
		}
	}

	def itemWithTargetRelation() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(ComposedTypeModel) {
				def relation = Mock(RelationDescriptorModel) {
					getRelationType() >> Mock(RelationMetaTypeModel) {
						getTargetTypeRole() >> "products"
						getSourceTypeCardinality() >> RelationEndCardinalityEnum.MANY
						getTargetTypeCardinality() >> RelationEndCardinalityEnum.MANY
					}
					getRelationName() >> "RelationName"
				}
				getDeclaredattributedescriptors() >> Collections.singletonList(relation)
				getCode() >> "Product"
			}
		}
	}

	def enumerationItem(final String code) {
		def type = Mock(EnumerationMetaTypeModel) {
			getCode() >> code
		}

		Mock(IntegrationObjectItemModel) {
			getCode() >> code
			getType() >> type
		}
	}

	def item(final String code) {
		item(code, code)
	}

	def item(final String integrationCode, final String platformCode) {
		Mock(IntegrationObjectItemModel) {
			getCode() >> integrationCode
			getType() >> Mock(ComposedTypeModel) {
				getCode() >> platformCode
			}
			getUniqueAttributes() >> []
		}
	}
}
