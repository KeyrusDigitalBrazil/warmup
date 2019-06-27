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
import de.hybris.platform.integrationservices.search.WhereClauseCondition
import de.hybris.platform.odata2services.filter.impl.DefaultExpressionVisitor
import org.apache.olingo.odata2.api.edm.EdmLiteral
import org.apache.olingo.odata2.api.edm.EdmTyped
import org.apache.olingo.odata2.api.uri.expression.*
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultExpressionVisitorUnitTest extends Specification
{
	def filterExpressionVisitor = Mock(FilterExpressionVisitor)
	def binaryExpressionVisitor = Mock(BinaryExpressionVisitor)
	def memberExpressionVisitor = Mock(MemberExpressionVisitor)
	def propertyExpressionVisitor = Mock(PropertyExpressionVisitor)
	def literalExpressionVisitor = Mock(LiteralExpressionVisitor);

	def expressionVisitor = new DefaultExpressionVisitor()

	def setup()
	{
		expressionVisitor.setFilterExpressionVisitor(filterExpressionVisitor)
		expressionVisitor.setBinaryExpressionVisitor(binaryExpressionVisitor)
		expressionVisitor.setMemberExpressionVisitor(memberExpressionVisitor)
		expressionVisitor.setPropertyExpressionVisitor(propertyExpressionVisitor)
		expressionVisitor.setLiteralExpressionVisitor(literalExpressionVisitor)
	}

	@Test
	def "filter expression visitor is called"()
	{
		given:
		def expression = Mock(FilterExpression)
		def expressionString = "expressionString"
		def result = Mock(WhereClauseCondition)

		when:
		expressionVisitor.visitFilterExpression(expression, expressionString, result)

		then:
		1 * filterExpressionVisitor.visit(expression, expressionString, result)
	}

	@Test
	def "binary expression visitor is called"()
	{
		given:
		def expression = Mock(BinaryExpression)
		def operator = BinaryOperator.EQ
		def left = "left"
		def right = "right"

		when:
		expressionVisitor.visitBinary(expression, operator, left, right)

		then:
		1 * binaryExpressionVisitor.visit(expression, operator, left, right)
	}

	@Test
	def "member expression visitor is called"()
	{
		given:
		def expression = Mock(MemberExpression)
		def path = "path"
		def property = "property"

		when:
		expressionVisitor.visitMember(expression, path, property)

		then:
		1 * memberExpressionVisitor.visit(expression, path, property)
	}

	@Test
	def "property expression visitor is called"()
	{
		given:
		def expression = Mock(PropertyExpression)
		def property = "property"
		def type = Mock(EdmTyped)

		when:
		expressionVisitor.visitProperty(expression, property, type)

		then:
		1 * propertyExpressionVisitor.visit(expression, property, type)
	}

	@Test
	def "literal expression visitor is called"()
	{
		given:
		def expression = Mock(LiteralExpression)
		def literal = GroovyMock(EdmLiteral)

		when:
		expressionVisitor.visitLiteral(expression, literal)

		then:
		1 * literalExpressionVisitor.visit(expression, literal)
	}

	@Test
	def "visitMethod() is not supported"()
	{
		when:
		expressionVisitor.visitMethod(null, null, null)

		then:
		thrown UnsupportedOperationException
	}

	@Test
	def "visitOrder() is not supported"()
	{
		when:
		expressionVisitor.visitOrder(null, null, null)

		then:
		thrown UnsupportedOperationException
	}

	@Test
	def "visitOrderByExpression is not supported"()
	{
		when:
		expressionVisitor.visitOrderByExpression(null, null, null)

		then:
		thrown UnsupportedOperationException
	}

	@Test
	def "visitUnary is not supported"()
	{
		when:
		expressionVisitor.visitUnary(null, null, null)

		then:
		thrown UnsupportedOperationException
	}
}
