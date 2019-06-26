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
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.search.WhereClauseCondition
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.uri.expression.*
import org.junit.Test
import spock.lang.Unroll

import static de.hybris.platform.core.PK.fromLong

@UnitTest
class NavigationPropertyWithIntegrationKeyVisitingStrategyUnitTest extends BaseNavigationPropertyVisitingStrategyUnitTest
{
	def integrationKeyConverter = Mock(IntegrationKeyToODataEntryGenerator)

	def setup()
	{
		strategy = new NavigationPropertyWithIntegrationKeyVisitingStrategy()
		strategy.setIntegrationKeyConverter(integrationKeyConverter)
		strategy.setItemLookupRequestFactory(itemLookupRequestFactory)
		strategy.setItemLookupStrategy(itemLookupStrategy)
	    strategy.setContext(context)
		strategy.setOperatorConverter(operatorConverter)
	}

	@Test
	@Unroll
	def "strategy isApplicable() == #applicable when left operand is of type #leftOpType, left is of type #leftType, and property is #property"()
	{
		expect:
		strategy.isApplicable(expression, null, left, null) == applicable

		where:
		expression                                           						| leftOpType         | left                     | leftType             | property			| applicable
		binaryExpression(MemberExpression, "unit", "integrationKey") 	| "MemberExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "integrationKey" 	| true
		binaryExpression(MemberExpression, "unit", "integrationKey") 	| "MemberExpression" | Mock(PropertyExpression) | "PropertyExpression" | "integrationKey"	| false
		binaryExpression(MemberExpression, "unit", "version")		 	| "MemberExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "version"			| false
		binaryExpression(BinaryExpression, "unit", "integrationKey") 	| "BinaryExpression" | Mock(EdmEntitySet)       | "EdmEntitySet"       | "integrationKey"	| false
	}

	@Test
	def "exception thrown during integration key generation"()
	{
		given:
		operatorConverter.convert(_) >> "="
		integrationKeyConverter.generate(_ as EdmEntitySet, _ as String) >> { throw new EdmException(EdmException.PROPERTYNOTFOUND) }

		when:
		strategy.visit(binaryExpression(MemberExpression, 'unit', 'integrationKey'), BinaryOperator.EQ, Mock(EdmEntitySet), "key|value")

		then:
		thrown(InternalProcessingException)
	}

	@Test
	def "no result condition during visit"()
	{
		given:
		operatorConverter.convert(_) >> "="
		integrationKeyConverter.generate(_ as EdmEntitySet, _ as String) >> Mock(ODataEntry)
		itemLookupRequestFactory.create(context, _ as EdmEntitySet, _ as ODataEntry, _ as String) >> Mock(ItemLookupRequest)
		itemLookupStrategy.lookup(_ as ItemLookupRequest) >> null

		when:
		def conditions = strategy.visit(binaryExpression(MemberExpression, 'unit', 'integrationKey'), BinaryOperator.EQ, Mock(EdmEntitySet), "key|value")

		then:
		conditions == new WhereClauseCondition("NO_RESULT").toWhereClauseConditions()
	}

	@Test
	def "query constructed correctly during visit strategy"()
	{
		given:
		def modelFound = Mock(ItemModel) { getPk() >> fromLong(1234L) }
		operatorConverter.convert(_) >> "="
		integrationKeyConverter.generate(_ as EdmEntitySet, _ as String) >> Mock(ODataEntry)
		itemLookupRequestFactory.create(context, _ as EdmEntitySet, _ as ODataEntry, _ as String) >> Mock(ItemLookupRequest)
		itemLookupStrategy.lookup(_ as ItemLookupRequest) >> modelFound

		when:
		def conditions = strategy.visit(binaryExpression(MemberExpression,'unit', 'integrationKey'), BinaryOperator.EQ, Mock(EdmEntitySet), "key|value")

		then:
		conditions.getConditions() == [new WhereClauseCondition('{unit} = 1234')]
	}

	def binaryExpression(Class operandType, path, property)
	{
		Mock(BinaryExpression) {
			getLeftOperand() >> Mock(operandType) {
				getProperty() >> Mock(PropertyExpression) {
					getUriLiteral() >> property
				}
				getPath() >> Mock(PropertyExpression) {
					getUriLiteral() >> path
				}
			}
		}
	}
}
