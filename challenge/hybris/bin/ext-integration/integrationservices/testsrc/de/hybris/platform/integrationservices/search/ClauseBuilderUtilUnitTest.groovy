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

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.enums.RelationEndCardinalityEnum
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.RelationDescriptorModel
import de.hybris.platform.core.model.type.RelationMetaTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class ClauseBuilderUtilUnitTest extends Specification {

	@Test
	@Unroll
	def "get AttributeDescriptorModel from filter #filter returns an Optional with isPresent equals #present"()
	{
		when:
		def result = ClauseBuilderUtil.getAttributeDescriptorModelFromFilterAndType(filter, item.getType())

		then:
		result.isPresent() == present

		where:
		filter 												| item  				| present
		null												| itemWithRelation()	| false
		new WhereClauseCondition("{source} = 1234")	| itemWithNoRelation()	| false
		new WhereClauseCondition("{target} = 1234")	| itemWithRelation()	| true
		new WhereClauseCondition("{source} = 1234")	| itemWithRelation()	| true
	}

	@Test
	@Unroll
	def "extract attribute name from filter with condition '#condition'"()
	{
		given:
		def filter = new WhereClauseCondition(condition)

		when:
		def name = ClauseBuilderUtil.extractAttributeNameFromFilter(filter)

		then:
		expectedName == name

		where:
		condition 		| expectedName
		"{code} = 123" 	| "code"
		""				| ""
		"code = 123"	| ""
	}

	@Test
	@Unroll
	def "extract attribute value from filter with condition '#condition'"()
	{
		given:
		def filter = new WhereClauseCondition(condition)

		when:
		def value = ClauseBuilderUtil.extractAttributeValueFromFilter(filter)

		then:
		expectedValue == value

		where:
		condition 		| expectedValue
		"{code} = 123" 	| "123"
		""				| ""
		"code * 123"	| ""
	}

	@Test
	@Unroll
	def "attribute '#attr' is source equals #isSource"()
	{
		given:
		def relationModel = manyToManyRelation("source", "target")

		expect:
		ClauseBuilderUtil.isAttributeSource(relationModel, attr) == isSource

		where:
		attr 		| isSource
		"source"	| true
		"target" 	| false
	}

	@Test
	def "get relation name alias"()
	{
		given:
		def relationModel = manyToManyRelation("source", "target")

		when:
		def alias = ClauseBuilderUtil.getRelationAlias(relationModel)

		then:
		"relationname" == alias
	}

	@Test
	def "get item alias"()
	{
		when:
		def alias = ClauseBuilderUtil.getItemAlias(itemWithRelation())

		then:
		"item" == alias
	}

	@Test
	@Unroll
	def "is many to many relation is #isManyToMany when attribute descriptor is #descriptor"()
	{
		expect:
		ClauseBuilderUtil.isManyToManyRelation(descriptor) == isManyToMany

		where:
		descriptor 											| isManyToMany
		manyToManyRelation("source", "target")	| true
		oneToManyRelation("source", 'target')	| false
	}

	@Test
	def "get relation name"()
	{
		expect:
		"RelationName" == ClauseBuilderUtil.getRelationName(oneToManyRelation("source", "target"))
	}

	RelationDescriptorModel manyToManyRelation(def source, def target)
	{
		relation(source, RelationEndCardinalityEnum.MANY, target, RelationEndCardinalityEnum.MANY)
	}

	RelationDescriptorModel oneToManyRelation(def source, def target)
	{
		relation(source, RelationEndCardinalityEnum.ONE, target, RelationEndCardinalityEnum.MANY)
	}

	RelationDescriptorModel relation(def source, def sourceCardinality, def target, def targetCardinality)
	{
		Stub(RelationDescriptorModel) {
			getRelationType() >> Stub(RelationMetaTypeModel) {
				getSourceTypeRole() >> source
				getTargetTypeRole() >> target
				getSourceTypeCardinality() >> sourceCardinality
				getTargetTypeCardinality() >> targetCardinality
			}
			getRelationName() >> "RelationName"
		}
	}

	IntegrationObjectItemModel itemWithRelation() {
		Stub(IntegrationObjectItemModel) {
			getType() >> Stub(ComposedTypeModel) {
					getDeclaredattributedescriptors() >> [manyToManyRelation("source", "target")]
					getCode() >> "Item"
			}
		}
	}

	IntegrationObjectItemModel itemWithNoRelation() {
		Stub(IntegrationObjectItemModel) {
			getType() >> Stub(ComposedTypeModel) {
					getDeclaredattributedescriptors() >> [Mock(AttributeDescriptorModel)]
			}
		}
	}
}
