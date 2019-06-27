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

import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;

/**
 * Defines the interface to visit a {@link BinaryExpression} and returns a {@link WhereClauseCondition}
 */
public interface BinaryExpressionVisitingStrategy
{
	boolean isApplicable(BinaryExpression expression, BinaryOperator operator, Object leftResult, Object rightResult);

	WhereClauseConditions visit(BinaryExpression expression, BinaryOperator operator, Object leftResult, Object rightResult);
}
