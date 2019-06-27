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

import org.apache.olingo.odata2.api.uri.expression.MemberExpression;

/**
 * Acts as a delegate to visiting the {@link MemberExpression}. The intended usage
 * of this interface is to call the visit method from the
 * {@link org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor#visitMember(MemberExpression, Object, Object)}
 */
public interface MemberExpressionVisitor
{
	/**
	 * Visit the {@link MemberExpression}
	 *
	 * @param expression Member expression
	 * @param pathResult Result from visiting the path
	 * @param propertyResult Result from visiting the property
	 * @return The result from this visit method
	 */
	Object visit(MemberExpression expression, Object pathResult, Object propertyResult);
}
