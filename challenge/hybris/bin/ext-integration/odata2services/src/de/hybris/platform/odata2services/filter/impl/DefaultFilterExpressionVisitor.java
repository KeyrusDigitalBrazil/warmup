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

import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.EMPTY_CONDITIONS;
import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.containsNoResultCondition;

import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.filter.FilterExpressionVisitor;
import de.hybris.platform.odata2services.filter.NoFilterResultException;

import org.apache.olingo.odata2.api.uri.expression.FilterExpression;

/**
 * Default implementation of the {@link FilterExpressionVisitor}
 */
public class DefaultFilterExpressionVisitor implements FilterExpressionVisitor
{
	@Override
	public WhereClauseConditions visit(final FilterExpression expression, final String expressionString, final Object result)
	{
		if (result instanceof WhereClauseConditions)
		{
			final WhereClauseConditions whereClauseConditions = (WhereClauseConditions) result;
			if (containsNoResultCondition(whereClauseConditions))
			{
				throw new NoFilterResultException();
			}
			return whereClauseConditions;
		}
		return EMPTY_CONDITIONS;
	}
}
