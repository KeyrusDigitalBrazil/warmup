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
package de.hybris.platform.odata2services.filter.impl;

import de.hybris.platform.odata2services.filter.BinaryExpressionVisitor;
import de.hybris.platform.odata2services.filter.FilterExpressionVisitor;
import de.hybris.platform.odata2services.filter.LiteralExpressionVisitor;
import de.hybris.platform.odata2services.filter.MemberExpressionVisitor;
import de.hybris.platform.odata2services.filter.PropertyExpressionVisitor;

import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryOperator;

/**
 * The DefaultExpressionVisitor is an {@link ExpressionVisitor} to
 * traverse an Olingo v2 {@link FilterExpression} tree.
 */
public class DefaultExpressionVisitor implements ExpressionVisitor
{
	private de.hybris.platform.odata2services.filter.BinaryExpressionVisitor binaryExpressionVisitor;
	private de.hybris.platform.odata2services.filter.FilterExpressionVisitor filterExpressionVisitor;
	private de.hybris.platform.odata2services.filter.LiteralExpressionVisitor literalExpressionVisitor;
	private de.hybris.platform.odata2services.filter.MemberExpressionVisitor memberExpressionVisitor;
	private de.hybris.platform.odata2services.filter.PropertyExpressionVisitor propertyExpressionVisitor;

	@Override
	public Object visitFilterExpression(final FilterExpression expression, final String expressionString, final Object result)
	{
		return getFilterExpressionVisitor().visit(expression, expressionString, result);
	}

	@Override
	public Object visitBinary(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		return getBinaryExpressionVisitor().visit(expression, operator, leftResult, rightResult);
	}

	@Override
	public Object visitLiteral(final LiteralExpression expression, final EdmLiteral literal)
	{
		return getLiteralExpressionVisitor().visit(expression, literal);
	}

	@Override
	public Object visitMember(final MemberExpression expression, final Object pathResult, final Object propertyResult)
	{
		return getMemberExpressionVisitor().visit(expression, pathResult, propertyResult);
	}

	@Override
	public Object visitProperty(final PropertyExpression expression, final String name, final EdmTyped type)
	{
		return getPropertyExpressionVisitor().visit(expression, name, type);
	}

	@Override
	public Object visitOrderByExpression(final OrderByExpression expression, final String expressionString, final List<Object> result)
	{
		throw new UnsupportedOperationException("visitOrderByExpression() is not supported");
	}

	@Override
	public Object visitOrder(final OrderExpression expression, final Object object, final SortOrder sortOrder)
	{
		throw new UnsupportedOperationException("visitOrder() is not supported");
	}

	@Override
	public Object visitMethod(final MethodExpression expression, final MethodOperator operator, final List<Object> result)
	{
		throw new UnsupportedOperationException("visitMethod() is not supported");
	}

	@Override
	public Object visitUnary(final UnaryExpression unaryExpression, final UnaryOperator unaryOperator, final Object o)
	{
		throw new UnsupportedOperationException("visitUnary() is not supported");
	}

	protected de.hybris.platform.odata2services.filter.BinaryExpressionVisitor getBinaryExpressionVisitor()
	{
		return binaryExpressionVisitor;
	}

	public void setBinaryExpressionVisitor(final BinaryExpressionVisitor binaryExpressionVisitor)
	{
		this.binaryExpressionVisitor = binaryExpressionVisitor;
	}

	protected de.hybris.platform.odata2services.filter.FilterExpressionVisitor getFilterExpressionVisitor()
	{
		return filterExpressionVisitor;
	}

	public void setFilterExpressionVisitor(final FilterExpressionVisitor filterExpressionVisitor)
	{
		this.filterExpressionVisitor = filterExpressionVisitor;
	}

	protected de.hybris.platform.odata2services.filter.LiteralExpressionVisitor getLiteralExpressionVisitor()
	{
		return literalExpressionVisitor;
	}

	public void setLiteralExpressionVisitor(final LiteralExpressionVisitor literalExpressionVisitor)
	{
		this.literalExpressionVisitor = literalExpressionVisitor;
	}

	protected de.hybris.platform.odata2services.filter.MemberExpressionVisitor getMemberExpressionVisitor()
	{
		return memberExpressionVisitor;
	}

	public void setMemberExpressionVisitor(final MemberExpressionVisitor memberExpressionVisitor)
	{
		this.memberExpressionVisitor = memberExpressionVisitor;
	}

	protected de.hybris.platform.odata2services.filter.PropertyExpressionVisitor getPropertyExpressionVisitor()
	{
		return propertyExpressionVisitor;
	}

	public void setPropertyExpressionVisitor(final PropertyExpressionVisitor propertyExpressionVisitor)
	{
		this.propertyExpressionVisitor = propertyExpressionVisitor;
	}
}
