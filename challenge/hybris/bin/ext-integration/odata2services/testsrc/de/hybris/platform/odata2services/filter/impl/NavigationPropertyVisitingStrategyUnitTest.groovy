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
package de.hybris.platform.odata2services.filter.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.search.WhereClauseCondition
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult
import org.apache.commons.lang3.tuple.Pair
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression
import org.apache.olingo.odata2.api.uri.expression.MemberExpression
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class NavigationPropertyVisitingStrategyUnitTest extends BaseNavigationPropertyVisitingStrategyUnitTest
{
	def setup()
	{
		strategy = new NavigationPropertyVisitingStrategy()
		strategy.setContext(context)
		strategy.setItemLookupRequestFactory(itemLookupRequestFactory)
		strategy.setItemLookupStrategy(itemLookupStrategy)
		strategy.setOperatorConverter(operatorConverter)
	}

	@Test
	@Unroll
	def "strategy isApplicable() == #applicable when left operand is of type #leftOpType, left is of type #leftType, and property is #property"()
	{
		expect:
		strategy.isApplicable(expression, null, left, null) == applicable

		where:
		expression                                                    | leftOpType         | left                     | leftType             | property         | applicable
		binaryExpression(MemberExpression, "version")        | "MemberExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "version"        | true
		binaryExpression(MemberExpression, "version")        | "MemberExpression" | Mock(PropertyExpression) | "PropertyExpression" | "version"        | false
		binaryExpression(MemberExpression, "integrationKey") | "MemberExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "integrationKey" | false
		binaryExpression(BinaryExpression, "version")        | "BinaryExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "version"        | false
	}

	@Test
	def "NO_RESULT where clause condition is returned when no items found during visit"()
	{
		given:
		def expression = binaryExpression(MemberExpression, "version")
		def left = Mock(EdmEntitySet)
		def itemLookupRequest = Mock(ItemLookupRequest)

		operatorConverter.convert(_) >> "="

		when:
		def result = strategy.visit(expression, null, left, null)

		then:
		1 * itemLookupRequestFactory.create(context, left, _ as Pair) >> itemLookupRequest
		1 * itemLookupStrategy.lookupItems(itemLookupRequest) >> Mock(ItemLookupResult)
		result.getConditions() == [new WhereClauseCondition("NO_RESULT")]
	}

	@Test
	def "where clause condition is returned when items are found during visit"()
	{
		given:
		def expression = binaryExpression(MemberExpression, "version")
		def left = Mock(EdmEntitySet)
		def itemLookupRequest = Mock(ItemLookupRequest)
		def itemLookupResult = Mock(ItemLookupResult) {
			getTotalCount() >> 2
			getEntries() >> [itemModel(1234), itemModel(5678)]
		}
		def pair

		operatorConverter.convert(_) >> "="

		when:
		def result = strategy.visit(expression, null, left, "Staged")

		then:
		1 * itemLookupRequestFactory.create(context, left, _ as Pair) >> {args -> pair = args[2]; itemLookupRequest}
		1 * itemLookupStrategy.lookupItems(itemLookupRequest) >> itemLookupResult
		result.getConditions() == [new WhereClauseCondition("{catalogVersion} IN (1234,5678)")]
		pair.getLeft() == "version"
		pair.getRight() == "Staged"
	}

	@Test
	def "exception is thrown while visiting"()
	{
		given:
		def expression = binaryExpression(MemberExpression, "version")
		def left = Mock(EdmEntitySet)

		operatorConverter.convert(_) >> "="
		itemLookupRequestFactory.create(context, left, _ as Pair) >> {throw Mock(EdmException)}

		when:
		strategy.visit(expression, null, left, null)

		then:
		thrown InternalProcessingException
	}

	def binaryExpression(Class operandType, property)
	{
		Mock(BinaryExpression)
		{
			getLeftOperand() >> Mock(operandType) {
				getProperty() >> Mock(PropertyExpression) {
					getUriLiteral() >> property
				}
				getPath() >> Mock(PropertyExpression) {
					getUriLiteral() >> "catalogVersion"
				}
			}
		}
	}

	def itemModel(pk)
	{
		Mock(ItemModel) {
			getPk() >> new PK(pk)
		}
	}
}
