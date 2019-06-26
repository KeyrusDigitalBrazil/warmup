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
package de.hybris.platform.odata2services.filter;

import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;

/**
 * Acts as a delegate to visiting the {@link BinaryExpression}. The intended usage
 * of this interface is to call the visit method from the
 * {@link org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor#visitBinary(BinaryExpression, BinaryOperator, Object, Object)}
 */
public interface BinaryExpressionVisitor
{
	/**
	 * Visit the {@link BinaryExpression}
	 *
	 * @param expression Binary expression
	 * @param operator Operator on the left and right operands
	 * @param leftResult Result from visiting the left operand
	 * @param rightResult Result from visiting the right operand
	 * @return The result from this visit method
	 */
	Object visit(BinaryExpression expression, BinaryOperator operator, Object leftResult, Object rightResult);
}
