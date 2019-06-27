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

import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.NO_RESULT_CONDITIONS;
import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.containsNoResultCondition;

import de.hybris.platform.integrationservices.search.ConjunctiveOperator;
import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.filter.BinaryExpressionVisitingStrategy;

import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.springframework.core.convert.converter.Converter;

/**
 * This strategy combines {@link WhereClauseCondition}s together using the {@link BinaryOperator} between them
 */
public class CombineWhereClauseConditionVisitingStrategy implements BinaryExpressionVisitingStrategy
{
	private Converter<BinaryOperator, String> operatorConverter;

	@Override
	public boolean isApplicable(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		return leftResult instanceof WhereClauseConditions && rightResult instanceof WhereClauseConditions;
	}

	@Override
	public WhereClauseConditions visit(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		final WhereClauseConditions leftConditions = (WhereClauseConditions) leftResult;
		final WhereClauseConditions rightConditions = (WhereClauseConditions) rightResult;

		if (bothConditionsHaveResults(leftConditions, rightConditions))
		{
			return leftConditions.join(rightConditions, ConjunctiveOperator.fromString(getOperatorConverter().convert(operator)));
		}
		else if (conditionHasResultForOrOperator(operator, leftConditions))
		{
			return leftConditions;
		}
		else if (conditionHasResultForOrOperator(operator, rightConditions))
		{
			return rightConditions;
		}
		return NO_RESULT_CONDITIONS;
	}

	private boolean conditionHasResultForOrOperator(final BinaryOperator operator, final WhereClauseConditions whereClauseConditions)
	{
		return !containsNoResultCondition(whereClauseConditions) && BinaryOperator.OR.equals(operator);
	}

	private boolean bothConditionsHaveResults(final WhereClauseConditions leftCondition, final WhereClauseConditions rightCondition)
	{
		return !containsNoResultCondition(leftCondition) && !containsNoResultCondition(rightCondition);
	}

	protected Converter<BinaryOperator, String> getOperatorConverter()
	{
		return operatorConverter;
	}

	public void setOperatorConverter(final Converter<BinaryOperator, String> operatorConverter)
	{
		this.operatorConverter = operatorConverter;
	}
}
