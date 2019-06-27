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

import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;

/**
 * Acts as a delegate to visiting the {@link FilterExpression}. The intended usage
 * of this interface is to call the visit method from the
 * {@link org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor#visitFilterExpression(FilterExpression, String, Object)}
 */
public interface FilterExpressionVisitor
{
	/**
	 * Visit the {@link FilterExpression}
	 *
	 * @param expression Filter expression
	 * @param expressionString String representation of the FilterExpression
	 * @param result Result from visiting other {@link CommonExpression}s
	 * @return The resulting {@link WhereClauseCondition} from visiting the FilterExpression
	 */
	WhereClauseConditions visit(FilterExpression expression, String expressionString, Object result);
}
