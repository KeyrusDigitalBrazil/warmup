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
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.RelationDescriptorModel
import de.hybris.platform.core.model.type.RelationMetaTypeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class FromClauseBuilderUnitTest extends Specification {

	def builder = FromClauseBuilder.builder()

	@Test
	@Unroll
	def "from clause for many-to-many relationship with JOIN for #end"() {
		given:
		def condition = new WhereClauseCondition('{supercategories} = 1234')
		def filter = new WhereClauseConditions(condition)

		when:
		def query = builder
				.withFilter(filter)
				.withIntegrationObjectItem(item)
				.build()

		then:
		query == "SELECT {product:pk} FROM {Product* AS product JOIN RelationName AS relationname ON {product:pk} = {relationname:" + end + "}}"

		where:
		end      | item
		'target' | sourceItem()
		'source' | targetItem()
	}

	@Test
	def "from clause for enum type item"()
	{
		given:
		def condition = new WhereClauseCondition('{code} = enum2')
		def filter = new WhereClauseConditions(condition)

		when:
		def query = builder
				.withFilter(filter)
				.withIntegrationObjectItem(enumItem())
				.build()

		then:
		query == "SELECT {myenum:pk} FROM {MyEnum AS myenum}"
	}

	@Test
	def "from clause for multiple relations item"()
	{
		given:
		def condition1 = new WhereClauseCondition('{supercategories} = cat1')
		def condition2 = new WhereClauseCondition('{vendors} = vendor1')
		def filter = new WhereClauseConditions(condition1, condition2)

		when:
		def query = builder
				.withFilter(filter)
				.withIntegrationObjectItem(multiRelationItem())
				.build()

		then:
		query == "SELECT {product:pk} FROM {Product* AS product JOIN CategoryProductRelation AS categoryproductrelation ON {product:pk} = {categoryproductrelation:target} JOIN ProductVendorRelation AS productvendorrelation ON {product:pk} = {productvendorrelation:source}}"
	}

	@Test
	@Unroll
	def "from clause with type hierarchy restriction"() {
		given:
		def condition = new WhereClauseCondition('{code} = mycode')
		def filter = new WhereClauseConditions(condition)

		when:
		def query = builder
				.withFilter(filter)
				.withIntegrationObjectItem(item())
				.withTypeHierarchyRestriction(itemTypeMatch)
				.build()

		then:
		query == "SELECT {mycode:pk} FROM {MyCode" + end + " AS mycode}"

		where:
		end | itemTypeMatch
		'*' | ItemTypeMatch.ALL_SUB_AND_SUPER_TYPES
		''  | ItemTypeMatch.ALL_SUBTYPES
		'!' | ItemTypeMatch.RESTRICT_TO_ITEM_TYPE
	}

	private IntegrationObjectItemModel sourceItem() {
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
				getDeclaredattributedescriptors() >> [relation]
				getCode() >> "Product"
			}
		}
	}

	private IntegrationObjectItemModel targetItem() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(ComposedTypeModel) {
				def relation = Mock(RelationDescriptorModel) {
					getRelationType() >> Mock(RelationMetaTypeModel) {
						getTargetTypeRole() >> "supercategories"
						getSourceTypeCardinality() >> RelationEndCardinalityEnum.MANY
						getTargetTypeCardinality() >> RelationEndCardinalityEnum.MANY
					}
					getRelationName() >> "RelationName"
				}
				getDeclaredattributedescriptors() >> [relation]
				getCode() >> "Product"
			}
		}
	}

	private IntegrationObjectItemModel enumItem() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(EnumerationMetaTypeModel) {
				getDeclaredattributedescriptors() >> []
				getCode() >> "MyEnum"
			}
		}
	}

	private IntegrationObjectItemModel item() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(ComposedTypeModel) {
				getDeclaredattributedescriptors() >> []
				getCode() >> "MyCode"
			}
		}
	}

	private IntegrationObjectItemModel multiRelationItem() {
		Mock(IntegrationObjectItemModel) {
			getType() >> Mock(ComposedTypeModel) {
				def relation1 = Mock(RelationDescriptorModel) {
					getRelationType() >> Mock(RelationMetaTypeModel) {
						getSourceTypeRole() >> "supercategories"
						getSourceTypeCardinality() >> RelationEndCardinalityEnum.MANY
						getTargetTypeCardinality() >> RelationEndCardinalityEnum.MANY
					}
					getRelationName() >> "CategoryProductRelation"
				}

				def relation2 = Mock(RelationDescriptorModel) {
					getRelationType() >> Mock(RelationMetaTypeModel) {
						getTargetTypeRole() >> "vendors"
						getSourceTypeCardinality() >> RelationEndCardinalityEnum.MANY
						getTargetTypeCardinality() >> RelationEndCardinalityEnum.MANY
					}
					getRelationName() >> "ProductVendorRelation"
				}
				getDeclaredattributedescriptors() >> [relation1, relation2]
				getCode() >> "Product"
			}
		}
	}
}
