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
package de.hybris.platform.odata2services.filter

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.search.WhereClauseConditions
import de.hybris.platform.odata2services.filter.impl.DefaultBinaryExpressionVisitor
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultBinaryExpressionVisitorUnitTest extends Specification
{
	def EMPTY_CONDITIONS = new WhereClauseConditions(Collections.emptyList())
	def binaryExpressionVisitingStrategy1 = Mock(BinaryExpressionVisitingStrategy)
	def binaryExpressionVisitingStrategy2 = Mock(BinaryExpressionVisitingStrategy)

	def left = "name"
	def right = "productName1"
	def expression = Mock(BinaryExpression)
	def operator = BinaryOperator.EQ

	def binaryExpressionVisitor = new DefaultBinaryExpressionVisitor()

	@Test
	def "empty condition returned when no strategy isApplicable"()
	{
		given:
		setStrategies(binaryExpressionVisitingStrategy1)

		binaryExpressionVisitingStrategy1.isApplicable(expression, operator, left, right) >> false

		when:
		def result = binaryExpressionVisitor.visit(expression, operator, left, right)

		then:
		1 * binaryExpressionVisitingStrategy1.isApplicable(expression, operator, left, right)
		result == EMPTY_CONDITIONS
	}

	@Test
	def "visit returns where clause condition"()
	{
		given:
		setStrategies(binaryExpressionVisitingStrategy1, binaryExpressionVisitingStrategy2)
		def expectedWhereClauseCondition = Mock(WhereClauseConditions)

		binaryExpressionVisitingStrategy1.isApplicable(expression, operator, left, right) >> true
		binaryExpressionVisitingStrategy2.isApplicable(expression, operator, left, right) >> false

		when:
		def result = binaryExpressionVisitor.visit(expression, operator, left, right)

		then:
		1 * binaryExpressionVisitingStrategy1.isApplicable(expression, operator, left, right) >> true
		1 * binaryExpressionVisitingStrategy2.isApplicable(expression, operator, left, right) >> false
		1 * binaryExpressionVisitingStrategy1.visit(expression, operator, left, right) >> expectedWhereClauseCondition
		result == expectedWhereClauseCondition
	}

	@Test
	def "exception thrown when more than 1 strategy isApplicable"()
	{
		given:
		setStrategies(binaryExpressionVisitingStrategy1, binaryExpressionVisitingStrategy2)
		binaryExpressionVisitingStrategy1.isApplicable(expression, operator, left, right) >> true
		binaryExpressionVisitingStrategy2.isApplicable(expression, operator, left, right) >> true

		when:
		binaryExpressionVisitor.visit(expression, operator, left, right)

		then:
		thrown InternalProcessingException
	}

	def setStrategies(BinaryExpressionVisitingStrategy... binaryExpressionVisitingStrategies)
	{
		binaryExpressionVisitor.setStrategies(binaryExpressionVisitingStrategies.flatten().collect())
	}
}
